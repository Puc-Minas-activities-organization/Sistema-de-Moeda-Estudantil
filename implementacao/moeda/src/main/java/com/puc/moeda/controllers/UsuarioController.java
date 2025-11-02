package com.puc.moeda.controllers;

import com.puc.moeda.dto.AlunoRequest;
import com.puc.moeda.dto.EmpresaParceiraRequest;
import com.puc.moeda.models.Role;
import com.puc.moeda.services.UsuarioServiceFactory;
import com.puc.moeda.services.interfaces.AutoCadastravel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioServiceFactory usuarioServiceFactory;

    @PostMapping("/cadastrar/aluno")
    public ResponseEntity<?> cadastrarAluno(@RequestBody AlunoRequest request) {
        try {
            // Verificar se o tipo pode se auto-cadastrar
            if (!usuarioServiceFactory.podeSeAutoCadastrar(Role.ALUNO)) {
                return ResponseEntity.badRequest()
                    .body("Alunos não podem se auto-cadastrar");
            }

            // Obter o service adequado usando o factory e fazer o cast correto
            @SuppressWarnings("unchecked")
            AutoCadastravel<?, AlunoRequest, ?> service = 
                (AutoCadastravel<?, AlunoRequest, ?>) usuarioServiceFactory.getAutoCadastravelService(Role.ALUNO);
            
            // Executar o cadastro
            Object resultado = service.cadastrar(request);
            
            return ResponseEntity.ok(resultado);
            
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro no cadastro: " + e.getMessage());
        }
    }

    @PostMapping("/cadastrar/empresa")
    public ResponseEntity<?> cadastrarEmpresa(@RequestBody EmpresaParceiraRequest request) {
        try {
            if (!usuarioServiceFactory.podeSeAutoCadastrar(Role.EMPRESA_PARCEIRA)) {
                return ResponseEntity.badRequest()
                    .body("Empresas não podem se auto-cadastrar");
            }

            @SuppressWarnings("unchecked")
            AutoCadastravel<?, EmpresaParceiraRequest, ?> service = 
                (AutoCadastravel<?, EmpresaParceiraRequest, ?>) usuarioServiceFactory.getAutoCadastravelService(Role.EMPRESA_PARCEIRA);
            Object resultado = service.cadastrar(request);
            
            return ResponseEntity.ok(resultado);
            
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro no cadastro: " + e.getMessage());
        }
    }

    @GetMapping("/perfil/{email}")
    public ResponseEntity<?> consultarPerfil(@PathVariable String email, 
                                           @RequestParam Role tipoUsuario) {
        try {
            var service = usuarioServiceFactory.getUsuarioService(tipoUsuario);
            Object perfil = service.consultarPerfil(email);
            return ResponseEntity.ok(perfil);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao consultar perfil: " + e.getMessage());
        }
    }
}