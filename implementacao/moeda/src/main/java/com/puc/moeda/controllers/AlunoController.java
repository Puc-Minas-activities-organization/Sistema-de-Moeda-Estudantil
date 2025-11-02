package com.puc.moeda.controllers;

import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.dto.ResgateResponse;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.services.BeneficioService;
import com.puc.moeda.services.ExtratoService;
import com.puc.moeda.services.ResgateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aluno")
@PreAuthorize("hasRole('ALUNO')")  // Protege TODA a classe
public class AlunoController {
    
    @Autowired
    private BeneficioService beneficioService;
    
    @Autowired
    private ResgateService resgateService;
    
    @Autowired
    private ExtratoService extratoService;
    
    /**
     * Listar benefícios disponíveis
     * GET /api/aluno/beneficios
     */
    @GetMapping("/beneficios")
    public ResponseEntity<?> listarBeneficios() {
        try {
            List<Beneficio> beneficios = beneficioService.listarBeneficiosAtivos();
            return ResponseEntity.ok(beneficios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao listar benefícios: " + e.getMessage()));
        }
    }
    
    /**
     * Resgatar benefício
     * POST /api/aluno/resgatar/{beneficioId}
     */
    @PostMapping("/resgatar/{beneficioId}")
    public ResponseEntity<?> resgatarBeneficio(
            @AuthenticationPrincipal Aluno aluno,
            @PathVariable Long beneficioId) {
        try {
            ResgateResponse resgate = resgateService.resgatarBeneficio(aluno, beneficioId);
            return ResponseEntity.ok(resgate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao resgatar benefício: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar extrato completo
     * GET /api/aluno/extrato
     */
    @GetMapping("/extrato")
    public ResponseEntity<?> consultarExtrato(@AuthenticationPrincipal Aluno aluno) {
        try {
            List<ExtratoItemDTO> extrato = extratoService.consultarExtratoAluno(aluno);
            return ResponseEntity.ok(new ExtratoResponse(
                aluno.getSaldoMoedas(),
                extrato
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao consultar extrato: " + e.getMessage()));
        }
    }
    
    /**
     * Listar meus resgates
     * GET /api/aluno/meus-resgates
     */
    @GetMapping("/meus-resgates")
    public ResponseEntity<?> listarMeusResgates(@AuthenticationPrincipal Aluno aluno) {
        try {
            List<ResgateBeneficio> resgates = resgateService.listarResgatesAluno(aluno);
            return ResponseEntity.ok(resgates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao listar resgates: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar saldo
     * GET /api/aluno/saldo
     */
    @GetMapping("/saldo")
    public ResponseEntity<?> consultarSaldo(@AuthenticationPrincipal Aluno aluno) {
        return ResponseEntity.ok(new SaldoResponse(
            aluno.getSaldoMoedas(),
            "Aluno: " + aluno.getNome()
        ));
    }
    
    /**
     * Consultar perfil
     * GET /api/aluno/perfil
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> consultarPerfil(@AuthenticationPrincipal Aluno aluno) {
        return ResponseEntity.ok(aluno);
    }
    
    // Records para responses
    record ErrorResponse(String message) {}
    record ExtratoResponse(Double saldoAtual, List<ExtratoItemDTO> transacoes) {}
    record SaldoResponse(Double saldo, String usuario) {}
}
