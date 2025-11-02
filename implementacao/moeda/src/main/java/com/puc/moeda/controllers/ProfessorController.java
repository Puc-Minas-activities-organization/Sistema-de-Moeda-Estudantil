package com.puc.moeda.controllers;

import com.puc.moeda.dto.EnviarMoedasRequest;
import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.models.Professor;
import com.puc.moeda.models.TransacaoMoeda;
import com.puc.moeda.services.ExtratoService;
import com.puc.moeda.services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
@PreAuthorize("hasRole('PROFESSOR')")  // Protege TODA a classe
public class ProfessorController {
    
    @Autowired
    private TransacaoService transacaoService;
    
    @Autowired
    private ExtratoService extratoService;
    
    /**
     * Enviar moedas para aluno
     * POST /api/professor/enviar-moedas
     */
    @PostMapping("/enviar-moedas")
    public ResponseEntity<?> enviarMoedas(
            @AuthenticationPrincipal Professor professor,
            @RequestBody EnviarMoedasRequest request) {
        try {
            TransacaoMoeda transacao = transacaoService.enviarMoedas(professor, request);
            return ResponseEntity.ok(new Response(
                "Moedas enviadas com sucesso!",
                transacao
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao enviar moedas: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar extrato
     * GET /api/professor/extrato
     */
    @GetMapping("/extrato")
    public ResponseEntity<?> consultarExtrato(@AuthenticationPrincipal Professor professor) {
        try {
            List<ExtratoItemDTO> extrato = extratoService.consultarExtratoProfessor(professor);
            return ResponseEntity.ok(new ExtratoResponse(
                professor.getSaldoMoedas(),
                extrato
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao consultar extrato: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar saldo
     * GET /api/professor/saldo
     */
    @GetMapping("/saldo")
    public ResponseEntity<?> consultarSaldo(@AuthenticationPrincipal Professor professor) {
        return ResponseEntity.ok(new SaldoResponse(
            professor.getSaldoMoedas(),
            "Professor: " + professor.getNome()
        ));
    }
    
    /**
     * Consultar perfil
     * GET /api/professor/perfil
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> consultarPerfil(@AuthenticationPrincipal Professor professor) {
        return ResponseEntity.ok(professor);
    }
    
    // Records para responses
    record Response(String message, Object data) {}
    record ErrorResponse(String message) {}
    record ExtratoResponse(Double saldoAtual, List<ExtratoItemDTO> transacoes) {}
    record SaldoResponse(Double saldo, String usuario) {}
}
