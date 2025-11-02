package com.puc.moeda.services;

import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.dto.ResgateResponse;
import com.puc.moeda.models.*;
import com.puc.moeda.repositories.AlunoRepository;
import com.puc.moeda.repositories.BeneficioRepository;
import com.puc.moeda.repositories.ResgateBeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResgateService {
    
    @Autowired
    private ResgateBeneficioRepository resgateRepository;
    
    @Autowired
    private BeneficioRepository beneficioRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private NotificacaoService notificacaoService;
    
    /**
     * REGRA DE NEGÓCIO: Aluno resgata benefício
     * - Aluno deve ter saldo suficiente
     * - Valor é descontado do saldo do aluno
     * - Código único é gerado para conferência
     * - Emails são enviados para aluno e empresa parceira
     */
    @Transactional
    public ResgateResponse resgatarBeneficio(Aluno aluno, Long beneficioId) {
        // Buscar benefício
        Beneficio beneficio = beneficioRepository.findById(beneficioId)
            .orElseThrow(() -> new RuntimeException("Benefício não encontrado"));
        
        // Verificar se está ativo
        if (!beneficio.getAtivo()) {
            throw new IllegalArgumentException("Este benefício não está mais disponível");
        }
        
        // Verificar saldo do aluno
        if (aluno.getSaldoMoedas() < beneficio.getCusto()) {
            throw new IllegalArgumentException(
                String.format("Saldo insuficiente. Você possui %.2f moedas e o benefício custa %.2f moedas", 
                    aluno.getSaldoMoedas(), beneficio.getCusto())
            );
        }
        
        // Criar resgate
        ResgateBeneficio resgate = new ResgateBeneficio();
        resgate.setAluno(aluno);
        resgate.setBeneficio(beneficio);
        resgate.setValorPago(beneficio.getCusto());
        resgate.setStatus(StatusResgate.PENDENTE);
        
        // Descontar moedas do aluno
        aluno.setSaldoMoedas(aluno.getSaldoMoedas() - beneficio.getCusto());
        
        // Salvar
        resgateRepository.save(resgate);
        alunoRepository.save(aluno);
        
        // Enviar notificações
        notificacaoService.notificarResgateAluno(resgate);
        notificacaoService.notificarResgateEmpresa(resgate);
        
        // Retornar response
        return new ResgateResponse(
            resgate.getId(),
            resgate.getCodigoResgate(),
            beneficio.getNome(),
            resgate.getValorPago(),
            resgate.getDataResgate(),
            resgate.getStatus(),
            "Resgate realizado com sucesso! Verifique seu email para o código."
        );
    }
    
    /**
     * Consultar resgate por código
     */
    public ResgateBeneficio consultarPorCodigo(String codigo) {
        return resgateRepository.findByCodigoResgate(codigo)
            .orElseThrow(() -> new RuntimeException("Código de resgate não encontrado"));
    }
    
    /**
     * Listar resgates do aluno
     */
    public List<ResgateBeneficio> listarResgatesAluno(Aluno aluno) {
        return resgateRepository.findByAlunoOrderByDataResgateDesc(aluno);
    }
    
    /**
     * Listar resgates da empresa
     */
    public List<ResgateBeneficio> listarResgatesEmpresa(Long empresaId) {
        return resgateRepository.findByEmpresaId(empresaId);
    }
    
    /**
     * Marcar resgate como usado (empresa confirma)
     */
    @Transactional
    public void confirmarUsoResgate(String codigo, EmpresaParceira empresa) {
        ResgateBeneficio resgate = consultarPorCodigo(codigo);
        
        // Verificar se o resgate pertence à empresa
        if (!resgate.getBeneficio().getEmpresaParceira().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("Este resgate não pertence a sua empresa");
        }
        
        // Verificar se já foi usado
        if (resgate.getStatus() == StatusResgate.USADO) {
            throw new IllegalArgumentException("Este resgate já foi utilizado");
        }
        
        resgate.marcarComoUsado();
        resgateRepository.save(resgate);
    }
    
    /**
     * Consultar extrato de resgates do aluno
     */
    public List<ExtratoItemDTO> consultarExtratoResgates(Aluno aluno) {
        List<ResgateBeneficio> resgates = resgateRepository.findByAlunoOrderByDataResgateDesc(aluno);
        List<ExtratoItemDTO> extrato = new ArrayList<>();
        
        for (ResgateBeneficio r : resgates) {
            ExtratoItemDTO item = new ExtratoItemDTO();
            item.setTipo("RESGATE");
            item.setValor(-r.getValorPago()); // Negativo pois é gasto
            item.setDescricao("Resgate: " + r.getBeneficio().getNome() + " (Código: " + r.getCodigoResgate() + ")");
            item.setData(r.getDataResgate());
            extrato.add(item);
        }
        
        return extrato;
    }
}
