package com.puc.moeda.controllers;

import com.puc.moeda.dto.AlunoRequest;
import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.dto.ResgateResponse;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.repositories.AlunoRepository;
import com.puc.moeda.services.AlunoService;
import com.puc.moeda.services.BeneficioService;
import com.puc.moeda.services.ExtratoService;
import com.puc.moeda.services.ResgateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aluno")
public class AlunoController {
  @Autowired AlunoService alunoService;

  @Autowired private BeneficioService beneficioService;

  @Autowired private ResgateService resgateService;

  @Autowired private ExtratoService extratoService;

  @Autowired private AlunoRepository alunoRepository;

  @DeleteMapping("/deletar-conta")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> deletarConta(@AuthenticationPrincipal Aluno aluno) {
    try {
      alunoService.deletarConta(aluno.getEmail());
      return ResponseEntity.ok(new Response("Conta de aluno deletada com sucesso!", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao deletar conta: " + e.getMessage()));
    }
  }


  /** Listar benefícios disponíveis GET /api/aluno/beneficios */
  @GetMapping("/beneficios")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> listarBeneficios() {
    try {
      List<Beneficio> beneficios = beneficioService.listarBeneficiosAtivos();
      return ResponseEntity.ok(beneficios);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao listar benefícios: " + e.getMessage()));
    }
  }

  /** Resgatar benefício POST /api/aluno/resgatar/{beneficioId} */
  @PostMapping("/resgatar/{beneficioId}")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> resgatarBeneficio(
      @AuthenticationPrincipal Aluno aluno, @PathVariable Long beneficioId) {
    try {
      ResgateResponse resgate = resgateService.resgatarBeneficio(aluno, beneficioId);
      return ResponseEntity.ok(resgate);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao resgatar benefício: " + e.getMessage()));
    }
  }

  /** Consultar extrato completo GET /api/aluno/extrato */
  @GetMapping("/extrato")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> consultarExtrato(@AuthenticationPrincipal Aluno aluno) {
    try {
      List<ExtratoItemDTO> extrato = extratoService.consultarExtratoAluno(aluno);
      return ResponseEntity.ok(new ExtratoResponse(aluno.getSaldoMoedas(), extrato));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao consultar extrato: " + e.getMessage()));
    }
  }

  /** Listar meus resgates GET /api/aluno/meus-resgates */
  @GetMapping("/meus-resgates")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> listarMeusResgates(@AuthenticationPrincipal Aluno aluno) {
    try {
      List<ResgateBeneficio> resgates = resgateService.listarResgatesAluno(aluno);
      return ResponseEntity.ok(resgates);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao listar resgates: " + e.getMessage()));
    }
  }

  /** Consultar saldo GET /api/aluno/saldo */
  @GetMapping("/saldo")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> consultarSaldo(@AuthenticationPrincipal Aluno aluno) {
    return ResponseEntity.ok(
        new SaldoResponse(aluno.getSaldoMoedas(), "Aluno: " + aluno.getNome()));
  }

  /** Consultar perfil GET /api/aluno/perfil */
  @GetMapping("/perfil")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> consultarPerfil(@AuthenticationPrincipal Aluno aluno) {
    return ResponseEntity.ok(aluno);
  }

  /** Atualizar perfil do aluno (próprio aluno) PUT /api/aluno/perfil */
  @PutMapping("/perfil")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> atualizarPerfil(
      @AuthenticationPrincipal Aluno aluno, @RequestBody AtualizarAlunoRequest request) {
    try {

      AlunoRequest req = new AlunoRequest();
      req.setNome(request.nome());
      req.setEmail(request.email());
      req.setSenha(request.senha());
      req.setCpf(aluno.getCpf());
      req.setRg(aluno.getRg());
      req.setEndereco(request.endereco());
      req.setInstituicao(aluno.getInstituicao());
      req.setCurso(request.curso());
      Aluno alunoSalvo = alunoService.alterarDados(aluno.getEmail(), req);
      return ResponseEntity.ok(new Response("Perfil atualizado com sucesso!", alunoSalvo));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao atualizar perfil: " + e.getMessage()));
    }
  }

  // ========== CRUD PÚBLICO ==========

  /** Listar todos os alunos GET /api/aluno/todos */
  @GetMapping("/todos")
  public ResponseEntity<List<Aluno>> listarTodosAlunos() {
    return ResponseEntity.ok(alunoRepository.findAll());
  }

  /** Buscar aluno por ID GET /api/aluno/{id} */
  @GetMapping("/{id}")
  public ResponseEntity<?> buscarAlunoPorId(@PathVariable Long id) {
    return alunoRepository
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /** Atualizar aluno por ID (público - qualquer um pode atualizar) PUT /api/aluno/{id} */
  @PutMapping("/{id}")
  public ResponseEntity<?> atualizarAluno(
      @PathVariable Long id, @RequestBody AtualizarAlunoAdminRequest request) {
    return alunoRepository
        .findById(id)
        .map(
            aluno -> {
              if (request.nome() != null) aluno.setNome(request.nome());
              if (request.cpf() != null) aluno.setCpf(request.cpf());
              if (request.rg() != null) aluno.setRg(request.rg());
              if (request.endereco() != null) aluno.setEndereco(request.endereco());
              if (request.curso() != null) aluno.setCurso(request.curso());
              if (request.instituicao() != null) aluno.setInstituicao(request.instituicao());
              if (request.saldoMoedas() != null) aluno.setSaldoMoedas(request.saldoMoedas());

              Aluno alunoSalvo = alunoRepository.save(aluno);
              return ResponseEntity.ok(new Response("Aluno atualizado com sucesso!", alunoSalvo));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  /** Deletar aluno DELETE /api/aluno/{id} */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletarAluno(@PathVariable Long id) {
    if (!alunoRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    alunoRepository.deleteById(id);
    return ResponseEntity.ok(new Response("Aluno deletado com sucesso!", null));
  }

  // Records para requests e responses
  record AtualizarAlunoRequest(
      String nome, String endereco, String curso, String email, String senha) {}

  record AtualizarAlunoAdminRequest(
      String nome,
      String cpf,
      String rg,
      String endereco,
      String curso,
      String instituicao,
      Double saldoMoedas) {}

  record Response(String message, Object data) {}

  record ErrorResponse(String message) {}

  record ExtratoResponse(Double saldoAtual, List<ExtratoItemDTO> transacoes) {}

  record SaldoResponse(Double saldo, String usuario) {}
}
