package com.puc.moeda.controllers;

import com.puc.moeda.dto.AlunoRequest;
import com.puc.moeda.dto.ExtratoItemDTO;
import com.puc.moeda.dto.NotificacaoDTO;
import com.puc.moeda.dto.ResgateResponse;
import com.puc.moeda.models.Aluno;
import com.puc.moeda.models.Beneficio;
import com.puc.moeda.models.ResgateBeneficio;
import com.puc.moeda.repositories.AlunoRepository;
import com.puc.moeda.services.AlunoService;
import com.puc.moeda.services.BeneficioService;
import com.puc.moeda.services.ExtratoService;
import com.puc.moeda.services.HistoricoNotificacaoService;
import com.puc.moeda.services.ResgateService;

import com.puc.moeda.dto.response.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aluno")
public class AlunoController {

  @Autowired private AlunoService alunoService;
  @Autowired private BeneficioService beneficioService;
  @Autowired private ResgateService resgateService;
  @Autowired private ExtratoService extratoService;
  @Autowired private AlunoRepository alunoRepository;
  @Autowired private HistoricoNotificacaoService historicoNotificacaoService;

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

  @GetMapping("/beneficios")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> listarBeneficios() {
    try {
      return ResponseEntity.ok(beneficioService.listarBeneficiosAtivos());
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao listar benefícios: " + e.getMessage()));
    }
  }

  @PostMapping("/resgatar/{beneficioId}")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> resgatarBeneficio(
      @AuthenticationPrincipal Aluno aluno, @PathVariable Long beneficioId) {
    try {
      ResgateResponse resgate = resgateService.resgatarBeneficio(aluno, beneficioId);
      return ResponseEntity.ok(resgate);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao resgatar benefício: " + e.getMessage()));
    }
  }

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

  @GetMapping("/meus-resgates")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> listarMeusResgates(@AuthenticationPrincipal Aluno aluno) {
    try {
      return ResponseEntity.ok(resgateService.listarResgatesAluno(aluno));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao listar resgates: " + e.getMessage()));
    }
  }

  @GetMapping("/notificacoes")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> listarNotificacoes(@AuthenticationPrincipal Aluno aluno) {
    try {
      List<NotificacaoDTO> notificacoes = historicoNotificacaoService.listarNotificacoes(aluno);
      Long naoLidas = historicoNotificacaoService.contarNaoLidas(aluno);
      return ResponseEntity.ok(new NotificacoesResponse(notificacoes, naoLidas));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao listar notificações: " + e.getMessage()));
    }
  }

  @GetMapping("/notificacoes/nao-lidas")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> listarNotificacoesNaoLidas(@AuthenticationPrincipal Aluno aluno) {
    try {
      return ResponseEntity.ok(historicoNotificacaoService.listarNotificacoesNaoLidas(aluno));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao listar notificações não lidas: " + e.getMessage()));
    }
  }

  @PutMapping("/notificacoes/{id}/lida")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> marcarComoLida(@PathVariable Long id) {
    try {
      historicoNotificacaoService.marcarComoLida(id);
      return ResponseEntity.ok(new Response("Notificação marcada como lida", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao marcar notificação: " + e.getMessage()));
    }
  }

  @PutMapping("/notificacoes/marcar-todas-lidas")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> marcarTodasComoLidas(@AuthenticationPrincipal Aluno aluno) {
    try {
      historicoNotificacaoService.marcarTodasComoLidas(aluno);
      return ResponseEntity.ok(new Response("Todas as notificações marcadas como lidas", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("Erro ao marcar notificações: " + e.getMessage()));
    }
  }

  @GetMapping("/saldo")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> consultarSaldo(@AuthenticationPrincipal Aluno aluno) {
    return ResponseEntity.ok(
        new SaldoResponse(aluno.getSaldoMoedas(), "Aluno: " + aluno.getNome()));
  }

  @GetMapping("/perfil")
  @PreAuthorize("hasRole('ALUNO')")
  public ResponseEntity<?> consultarPerfil(@AuthenticationPrincipal Aluno aluno) {
    return ResponseEntity.ok(aluno);
  }

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

  @GetMapping("/todos")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Aluno>> listarTodosAlunos() {
    return ResponseEntity.ok(alunoRepository.findAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('ALUNO') and #id == authentication.principal.id)")
  public ResponseEntity<?> buscarAlunoPorId(@PathVariable Long id) {
    return alunoRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('ALUNO') and #id == authentication.principal.id)")
  public ResponseEntity<?> atualizarAluno(
      @PathVariable Long id, @RequestBody AtualizarAlunoAdminRequest request) {
    return alunoRepository.findById(id)
        .map(aluno -> {

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

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('ALUNO') and #id == authentication.principal.id)")
  public ResponseEntity<?> deletarAluno(@PathVariable Long id) {
    if (!alunoRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    alunoRepository.deleteById(id);
    return ResponseEntity.ok(new Response("Aluno deletado com sucesso!", null));
  }
}
