package com.puc.moeda.services;

import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.models.*;
import com.puc.moeda.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Serviço para gerar extrato completo de transações
 */
@Service
public class ExtratoService {
    
    @Autowired
    private TransacaoMoedaRepository transacaoRepository;
    
    @Autowired
    private ResgateBeneficioRepository resgateRepository;
    
    /**
     * Extrato completo do aluno (recebimentos + resgates)
     */
    public List<ExtratoItemDTO> consultarExtratoAluno(Aluno aluno) {
        List<ExtratoItemDTO> extrato = new ArrayList<>();
        
        // Adicionar recebimentos
        List<TransacaoMoeda> recebimentos = transacaoRepository.findByDestinatarioOrderByDataTransacaoDesc(aluno);
        for (TransacaoMoeda t : recebimentos) {
            ExtratoItemDTO item = new ExtratoItemDTO();
            item.setTipo("RECEBIMENTO");
            item.setValor(t.getValor());
            item.setDescricao("Recebido de " + t.getRemetente().getNome() + ": " + t.getMensagem());
            item.setData(t.getDataTransacao());
            extrato.add(item);
        }
        
        // Adicionar resgates
        List<ResgateBeneficio> resgates = resgateRepository.findByAlunoOrderByDataResgateDesc(aluno);
        for (ResgateBeneficio r : resgates) {
            ExtratoItemDTO item = new ExtratoItemDTO();
            item.setTipo("RESGATE");
            item.setValor(-r.getValorPago());
            item.setDescricao("Resgate: " + r.getBeneficio().getNome() + " (Código: " + r.getCodigoResgate() + ")");
            item.setData(r.getDataResgate());
            extrato.add(item);
        }
        
        // Ordenar por data (mais recente primeiro)
        extrato.sort(Comparator.comparing(ExtratoItemDTO::getData).reversed());
        
        // Calcular saldo após cada transação
        double saldoAtual = aluno.getSaldoMoedas();
        for (int i = 0; i < extrato.size(); i++) {
            extrato.get(i).setSaldoAposTransacao(saldoAtual);
            if (i < extrato.size() - 1) {
                saldoAtual -= extrato.get(i).getValor();
            }
        }
        
        return extrato;
    }
    
    /**
     * Extrato do professor (apenas envios)
     */
    public List<ExtratoItemDTO> consultarExtratoProfessor(Professor professor) {
        List<ExtratoItemDTO> extrato = new ArrayList<>();
        
        List<TransacaoMoeda> envios = transacaoRepository.findByRemetenteOrderByDataTransacaoDesc(professor);
        double saldoAtual = professor.getSaldoMoedas();
        
        for (int i = 0; i < envios.size(); i++) {
            TransacaoMoeda t = envios.get(i);
            ExtratoItemDTO item = new ExtratoItemDTO();
            item.setTipo("ENVIO");
            item.setValor(-t.getValor());
            item.setDescricao("Enviado para " + t.getDestinatario().getNome() + ": " + t.getMensagem());
            item.setData(t.getDataTransacao());
            item.setSaldoAposTransacao(saldoAtual);
            extrato.add(item);
            
            if (i < envios.size() - 1) {
                saldoAtual += t.getValor(); // Adiciona porque estamos indo do presente para o passado
            }
        }
        
        return extrato;
    }
}
