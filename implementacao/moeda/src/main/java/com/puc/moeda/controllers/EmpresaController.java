package com.puc.moeda.controllers;

import com.puc.moeda.dto.BeneficioRequest;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.EmpresaParceira;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.repositories.EmpresaParceiraRepository;
import com.puc.moeda.services.BeneficioService;
import com.puc.moeda.services.EmpresaParceiraService;
import com.puc.moeda.services.ResgateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {
    @Autowired
    private EmpresaParceiraService empresaParceiraService;

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private ResgateService resgateService;

    @Autowired
    private EmpresaParceiraRepository empresaRepository;

    // ========== ENDPOINTS DA EMPRESA (requer autenticação EMPRESA_PARCEIRA)
    // ==========

    /** Cadastrar benefício POST /api/empresa/beneficios */
    @PostMapping("/beneficios")
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    public ResponseEntity<?> cadastrarBeneficio(
            @AuthenticationPrincipal EmpresaParceira empresa, @RequestBody BeneficioRequest request) {
        try {
            Beneficio beneficio = beneficioService.cadastrarBeneficio(empresa, request);
            return ResponseEntity.ok(new Response("Benefício cadastrado com sucesso!", beneficio));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao cadastrar benefício: " + e.getMessage()));
        }
    }

    /** Editar benefício PUT /api/empresa/beneficios/{id} */
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    @PutMapping("/beneficios/{id}")
    public ResponseEntity<?> editarBeneficio(
            @AuthenticationPrincipal EmpresaParceira empresa,
            @PathVariable Long id,
            @RequestBody BeneficioRequest request) {
        try {
            Beneficio beneficio = beneficioService.editarBeneficio(id, empresa, request);
            return ResponseEntity.ok(new Response("Benefício atualizado com sucesso!", beneficio));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao editar benefício: " + e.getMessage()));
        }
    }

    /** Remover benefício DELETE /api/empresa/beneficios/{id} */
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    @DeleteMapping("/beneficios/{id}")
    public ResponseEntity<?> removerBeneficio(
            @AuthenticationPrincipal EmpresaParceira empresa, @PathVariable Long id) {
        try {
            beneficioService.removerBeneficio(id, empresa);
            return ResponseEntity.ok(new Response("Benefício removido com sucesso!", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao remover benefício: " + e.getMessage()));
        }
    }

    /** Listar meus benefícios GET /api/empresa/beneficios */
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    @GetMapping("/beneficios")
    public ResponseEntity<?> listarMeusBeneficios(@AuthenticationPrincipal EmpresaParceira empresa) {
        try {
            List<Beneficio> beneficios = beneficioService.listarBeneficiosEmpresa(empresa);
            return ResponseEntity.ok(beneficios);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao listar benefícios: " + e.getMessage()));
        }
    }

    /** Consultar benefício específico GET /api/empresa/beneficios/{id} */
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    @GetMapping("/beneficios/{id}")
    public ResponseEntity<?> consultarBeneficio(@PathVariable Long id) {
        try {
            Beneficio beneficio = beneficioService.consultarBeneficio(id);
            return ResponseEntity.ok(beneficio);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao consultar benefício: " + e.getMessage()));
        }
    }

    /** Listar resgates dos meus benefícios GET /api/empresa/resgates */
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    @GetMapping("/resgates")
    public ResponseEntity<?> listarResgates(@AuthenticationPrincipal EmpresaParceira empresa) {
        try {
            List<ResgateBeneficio> resgates = resgateService.listarResgatesEmpresa(empresa.getId());
            return ResponseEntity.ok(resgates);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao listar resgates: " + e.getMessage()));
        }
    }

    /**
     * Confirmar uso de resgate (validar código) POST
     * /api/empresa/resgates/confirmar/{codigo}
     */
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    @PostMapping("/resgates/confirmar/{codigo}")
    public ResponseEntity<?> confirmarResgate(
            @AuthenticationPrincipal EmpresaParceira empresa, @PathVariable String codigo) {
        try {
            resgateService.confirmarUsoResgate(codigo, empresa);
            return ResponseEntity.ok(new Response("Resgate confirmado com sucesso!", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao confirmar resgate: " + e.getMessage()));
        }
    }

    /** Consultar perfil GET /api/empresa/perfil */
    @GetMapping("/perfil")
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    public ResponseEntity<?> consultarPerfil(@AuthenticationPrincipal EmpresaParceira empresa) {
        return ResponseEntity.ok(empresa);
    }

    /** Atualizar perfil da empresa PUT /api/empresa/perfil */
    @PutMapping("/perfil")
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    public ResponseEntity<?> atualizarPerfil(
            @AuthenticationPrincipal EmpresaParceira empresa,
            @RequestBody AtualizarEmpresaRequest request) {
        try {
            // Atualizar apenas campos permitidos
            if (request.nome() != null) {
                empresa.setNome(request.nome());
            }

            EmpresaParceira empresaSalva = empresaRepository.save(empresa);
            return ResponseEntity.ok(new Response("Perfil atualizado com sucesso!", empresaSalva));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao atualizar perfil: " + e.getMessage()));
        }
    }

    // ========== CRUD PROTEGIDO ==========

    /** Listar todas as empresas GET /api/empresa/todas (apenas ADMIN) */
    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmpresaParceira>> listarTodasEmpresas() {
        return ResponseEntity.ok(empresaRepository.findAll());
    }

    /** Buscar empresa por ID GET /api/empresa/{id} (apenas a própria empresa ou ADMIN) */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPRESA_PARCEIRA') and #id == authentication.principal.id)")
    public ResponseEntity<?> buscarEmpresaPorId(@PathVariable Long id) {
        return empresaRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualizar empresa por ID (apenas a própria empresa ou ADMIN) PUT /api/empresa/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPRESA_PARCEIRA') and #id == authentication.principal.id)")
    public ResponseEntity<?> atualizarEmpresa(
            @PathVariable Long id, @RequestBody AtualizarEmpresaAdminRequest request) {
        return empresaRepository
                .findById(id)
                .map(
                        empresa -> {
                            if (request.nome() != null)
                                empresa.setNome(request.nome());
                            if (request.cnpj() != null)
                                empresa.setCnpj(request.cnpj());

                            EmpresaParceira empresaSalva = empresaRepository.save(empresa);
                            return ResponseEntity.ok(
                                    new Response("Empresa atualizada com sucesso!", empresaSalva));
                        })
                .orElse(ResponseEntity.notFound().build());
    }

    /** Deletar empresa DELETE /api/empresa/{id} (apenas a própria empresa ou ADMIN) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPRESA_PARCEIRA') and #id == authentication.principal.id)")
    public ResponseEntity<?> deletarEmpresa(@PathVariable Long id) {
        if (!empresaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        empresaRepository.deleteById(id);
        return ResponseEntity.ok(new Response("Empresa deletada com sucesso!", null));
    }

    @DeleteMapping("/deletar-conta")
    @PreAuthorize("hasRole('EMPRESA_PARCEIRA')")
    public ResponseEntity<?> deletarConta(@AuthenticationPrincipal EmpresaParceira empresa) {
        try {
            empresaParceiraService.deletarConta(empresa.getEmail());
            return ResponseEntity.ok(new Response("Conta de empresa deletada com sucesso!", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erro ao deletar conta: " + e.getMessage()));
        }
    }

    // Records para requests e responses
    record AtualizarEmpresaRequest(String nome, String email, String senha, String endereco) {
    }

    record AtualizarEmpresaAdminRequest(String nome, String cnpj) {
    }

    record Response(String message, Object data) {
    }

    record ErrorResponse(String message) {
    }
}
