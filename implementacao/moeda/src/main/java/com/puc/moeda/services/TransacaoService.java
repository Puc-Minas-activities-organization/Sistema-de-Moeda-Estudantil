package com.puc.moeda.services;

import com.puc.moeda.dto.EnviarMoedasRequest;
import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.models.*;
import com.puc.moeda.repositories.AlunoRepository;
import com.puc.moeda.repositories.ProfessorRepository;
import com.puc.moeda.repositories.TransacaoMoedaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoService {
    
    private static final Double MOEDAS_POR_SEMESTRE = 1000.0;
    
    @Autowired
    private TransacaoMoedaRepository transacaoRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private NotificacaoService notificacaoService;
    
    /**
     * REGRA DE NEGÓCIO: Professor envia moedas para aluno
     * - Professor deve ter saldo suficiente
     * - Mensagem é obrigatória (motivo do reconhecimento)
     * - Aluno é notificado por email
     */
    @Transactional
    public TransacaoMoeda enviarMoedas(Professor professor, EnviarMoedasRequest request) {
        // Validações
        if (request.getValor() == null || request.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        
        if (request.getMensagem() == null || request.getMensagem().trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem é obrigatória para enviar moedas");
        }
        
        // Buscar aluno
        Aluno aluno = alunoRepository.findById(request.getAlunoId())
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        // Verificar saldo do professor
        if (professor.getSaldoMoedas() < request.getValor()) {
            throw new IllegalArgumentException(
                String.format("Saldo insuficiente. Você possui %.2f moedas", professor.getSaldoMoedas())
            );
        }
        
        // Criar transação
        TransacaoMoeda transacao = new TransacaoMoeda();
        transacao.setRemetente(professor);
        transacao.setDestinatario(aluno);
        transacao.setValor(request.getValor());
        transacao.setMensagem(request.getMensagem());
        transacao.setStatus(StatusTransacao.CONCLUIDA);
        
        // Atualizar saldos
        professor.setSaldoMoedas(professor.getSaldoMoedas() - request.getValor());
        aluno.setSaldoMoedas(aluno.getSaldoMoedas() + request.getValor());
        
        // Salvar
        transacaoRepository.save(transacao);
        professorRepository.save(professor);
        alunoRepository.save(aluno);
        
        // Notificar aluno
        notificacaoService.notificarRecebimentoMoedas(transacao);
        
        return transacao;
    }
    
    /**
     * REGRA DE NEGÓCIO: Adicionar moedas semestrais ao professor
     * - Todo semestre o professor recebe 1000 moedas
     * - É acumulativo (se não gastou tudo, recebe mais 1000)
     */
    @Transactional
    public void adicionarMoedasSemestrais(Professor professor) {
        professor.setSaldoMoedas(professor.getSaldoMoedas() + MOEDAS_POR_SEMESTRE);
        professorRepository.save(professor);
    }
    
    /**
     * Consultar extrato do professor
     */
    public List<ExtratoItemDTO> consultarExtratoProfessor(Professor professor) {
        List<TransacaoMoeda> transacoes = transacaoRepository.findByRemetenteOrderByDataTransacaoDesc(professor);
        List<ExtratoItemDTO> extrato = new ArrayList<>();
        
        for (TransacaoMoeda t : transacoes) {
            ExtratoItemDTO item = new ExtratoItemDTO();
            item.setTipo("ENVIO");
            item.setValor(-t.getValor()); // Negativo pois é envio
            item.setDescricao("Enviado para " + t.getDestinatario().getNome() + ": " + t.getMensagem());
            item.setData(t.getDataTransacao());
            extrato.add(item);
        }
        
        return extrato;
    }
    
    /**
     * Consultar extrato do aluno (apenas recebimentos)
     */
    public List<TransacaoMoeda> consultarTransacoesAluno(Aluno aluno) {
        return transacaoRepository.findByDestinatarioOrderByDataTransacaoDesc(aluno);
    }
}
