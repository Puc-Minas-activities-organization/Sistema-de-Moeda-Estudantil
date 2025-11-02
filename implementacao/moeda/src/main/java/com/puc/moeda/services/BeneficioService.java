package com.puc.moeda.services;

import com.puc.moeda.dto.BeneficioRequest;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.EmpresaParceira;
import com.puc.moeda.repositories.BeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficioService {
    
    @Autowired
    private BeneficioRepository beneficioRepository;
    
    /**
     * Cadastrar novo benefício
     */
    @Transactional
    public Beneficio cadastrarBeneficio(EmpresaParceira empresa, BeneficioRequest request) {
        // Validações
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do benefício é obrigatório");
        }
        
        if (request.getCusto() == null || request.getCusto() <= 0) {
            throw new IllegalArgumentException("Custo deve ser maior que zero");
        }
        
        if (request.getDescricao() == null || request.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(request.getNome());
        beneficio.setCusto(request.getCusto());
        beneficio.setDescricao(request.getDescricao());
        beneficio.setFoto(request.getFoto());
        beneficio.setEmpresaParceira(empresa);
        beneficio.setAtivo(true);
        
        return beneficioRepository.save(beneficio);
    }
    
    /**
     * Editar benefício
     */
    @Transactional
    public Beneficio editarBeneficio(Long beneficioId, EmpresaParceira empresa, BeneficioRequest request) {
        Beneficio beneficio = beneficioRepository.findById(beneficioId)
            .orElseThrow(() -> new RuntimeException("Benefício não encontrado"));
        
        // Verificar se o benefício pertence à empresa
        if (!beneficio.getEmpresaParceira().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("Este benefício não pertence a sua empresa");
        }
        
        // Atualizar campos
        if (request.getNome() != null) {
            beneficio.setNome(request.getNome());
        }
        if (request.getCusto() != null && request.getCusto() > 0) {
            beneficio.setCusto(request.getCusto());
        }
        if (request.getDescricao() != null) {
            beneficio.setDescricao(request.getDescricao());
        }
        if (request.getFoto() != null) {
            beneficio.setFoto(request.getFoto());
        }
        
        return beneficioRepository.save(beneficio);
    }
    
    /**
     * Remover (desativar) benefício
     */
    @Transactional
    public void removerBeneficio(Long beneficioId, EmpresaParceira empresa) {
        Beneficio beneficio = beneficioRepository.findById(beneficioId)
            .orElseThrow(() -> new RuntimeException("Benefício não encontrado"));
        
        // Verificar se o benefício pertence à empresa
        if (!beneficio.getEmpresaParceira().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("Este benefício não pertence a sua empresa");
        }
        
        // Desativar ao invés de deletar (soft delete)
        beneficio.setAtivo(false);
        beneficioRepository.save(beneficio);
    }
    
    /**
     * Listar todos os benefícios ativos
     */
    public List<Beneficio> listarBeneficiosAtivos() {
        return beneficioRepository.findByAtivoTrue();
    }
    
    /**
     * Listar benefícios de uma empresa
     */
    public List<Beneficio> listarBeneficiosEmpresa(EmpresaParceira empresa) {
        return beneficioRepository.findByEmpresaParceiraOrderByNomeAsc(empresa);
    }
    
    /**
     * Consultar benefício por ID
     */
    public Beneficio consultarBeneficio(Long id) {
        return beneficioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Benefício não encontrado"));
    }
}
