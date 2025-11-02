package com.puc.moeda.controllers;

import com.puc.moeda.dto.BeneficioRequest;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.EmpresaParceira;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.services.BeneficioService;
import com.puc.moeda.services.ResgateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresa")
@PreAuthorize("hasRole('EMPRESA_PARCEIRA')")  // Protege TODA a classe
public class EmpresaController {    @Autowired
    private BeneficioService beneficioService;
    
    @Autowired
    private ResgateService resgateService;
    
    /**
     * Cadastrar benefício
     * POST /api/empresa/beneficios
     */
    @PostMapping("/beneficios")
    public ResponseEntity<?> cadastrarBeneficio(
            @AuthenticationPrincipal EmpresaParceira empresa,
            @RequestBody BeneficioRequest request) {
        try {
            Beneficio beneficio = beneficioService.cadastrarBeneficio(empresa, request);
            return ResponseEntity.ok(new Response(
                "Benefício cadastrado com sucesso!",
                beneficio
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao cadastrar benefício: " + e.getMessage()));
        }
    }
    
    /**
     * Editar benefício
     * PUT /api/empresa/beneficios/{id}
     */
    @PutMapping("/beneficios/{id}")
    public ResponseEntity<?> editarBeneficio(
            @AuthenticationPrincipal EmpresaParceira empresa,
            @PathVariable Long id,
            @RequestBody BeneficioRequest request) {
        try {
            Beneficio beneficio = beneficioService.editarBeneficio(id, empresa, request);
            return ResponseEntity.ok(new Response(
                "Benefício atualizado com sucesso!",
                beneficio
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao editar benefício: " + e.getMessage()));
        }
    }
    
    /**
     * Remover benefício
     * DELETE /api/empresa/beneficios/{id}
     */
    @DeleteMapping("/beneficios/{id}")
    public ResponseEntity<?> removerBeneficio(
            @AuthenticationPrincipal EmpresaParceira empresa,
            @PathVariable Long id) {
        try {
            beneficioService.removerBeneficio(id, empresa);
            return ResponseEntity.ok(new Response(
                "Benefício removido com sucesso!",
                null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao remover benefício: " + e.getMessage()));
        }
    }
    
    /**
     * Listar meus benefícios
     * GET /api/empresa/beneficios
     */
    @GetMapping("/beneficios")
    public ResponseEntity<?> listarMeusBeneficios(@AuthenticationPrincipal EmpresaParceira empresa) {
        try {
            List<Beneficio> beneficios = beneficioService.listarBeneficiosEmpresa(empresa);
            return ResponseEntity.ok(beneficios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao listar benefícios: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar benefício específico
     * GET /api/empresa/beneficios/{id}
     */
    @GetMapping("/beneficios/{id}")
    public ResponseEntity<?> consultarBeneficio(@PathVariable Long id) {
        try {
            Beneficio beneficio = beneficioService.consultarBeneficio(id);
            return ResponseEntity.ok(beneficio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao consultar benefício: " + e.getMessage()));
        }
    }
    
    /**
     * Listar resgates dos meus benefícios
     * GET /api/empresa/resgates
     */
    @GetMapping("/resgates")
    public ResponseEntity<?> listarResgates(@AuthenticationPrincipal EmpresaParceira empresa) {
        try {
            List<ResgateBeneficio> resgates = resgateService.listarResgatesEmpresa(empresa.getId());
            return ResponseEntity.ok(resgates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao listar resgates: " + e.getMessage()));
        }
    }
    
    /**
     * Confirmar uso de resgate (validar código)
     * POST /api/empresa/resgates/confirmar/{codigo}
     */
    @PostMapping("/resgates/confirmar/{codigo}")
    public ResponseEntity<?> confirmarResgate(
            @AuthenticationPrincipal EmpresaParceira empresa,
            @PathVariable String codigo) {
        try {
            resgateService.confirmarUsoResgate(codigo, empresa);
            return ResponseEntity.ok(new Response(
                "Resgate confirmado com sucesso!",
                null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao confirmar resgate: " + e.getMessage()));
        }
    }
    
    /**
     * Consultar perfil
     * GET /api/empresa/perfil
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> consultarPerfil(@AuthenticationPrincipal EmpresaParceira empresa) {
        return ResponseEntity.ok(empresa);
    }
    
    // Records para responses
    record Response(String message, Object data) {}
    record ErrorResponse(String message) {}
}
