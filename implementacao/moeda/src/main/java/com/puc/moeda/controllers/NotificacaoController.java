package com.puc.moeda.controllers;

import com.puc.moeda.dto.NotificacaoDTO;
import com.puc.moeda.models.Notificacao;
import com.puc.moeda.models.Usuario;
import com.puc.moeda.repositories.NotificacaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar notificações/emails dos usuários
 */
@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificações", description = "Gerenciamento de notificações e histórico de emails")
@Slf4j
public class NotificacaoController {
    
    @Autowired
    private NotificacaoRepository notificacaoRepository;
    
    /**
     * Lista todas as notificações do usuário logado
     */
    @GetMapping
    @Operation(summary = "Listar notificações", description = "Retorna todas as notificações do usuário logado, ordenadas da mais recente para a mais antiga")
    public ResponseEntity<List<NotificacaoDTO>> listarMinhasNotificacoes(
            @AuthenticationPrincipal Usuario usuario) {
        
        List<Notificacao> notificacoes = notificacaoRepository
                .findByUsuarioOrderByDataEnvioDesc(usuario);
        
        List<NotificacaoDTO> dtos = notificacoes.stream()
                .map(this::converterParaDTO)
                .toList();
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Lista apenas notificações não lidas
     */
    @GetMapping("/nao-lidas")
    @Operation(summary = "Listar não lidas", description = "Retorna apenas as notificações não lidas do usuário")
    public ResponseEntity<List<NotificacaoDTO>> listarNaoLidas(
            @AuthenticationPrincipal Usuario usuario) {
        
        List<Notificacao> notificacoes = notificacaoRepository
                .findByUsuarioAndLidaFalseOrderByDataEnvioDesc(usuario);
        
        List<NotificacaoDTO> dtos = notificacoes.stream()
                .map(this::converterParaDTO)
                .toList();
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Marca uma notificação como lida
     */
    @PutMapping("/{id}/marcar-lida")
    @Operation(summary = "Marcar como lida", description = "Marca uma notificação específica como lida")
    public ResponseEntity<?> marcarComoLida(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElse(null);
        
        if (notificacao == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar se a notificação pertence ao usuário
        if (!notificacao.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).body("Você não tem permissão para marcar esta notificação");
        }
        
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Marca todas as notificações como lidas
     */
    @PutMapping("/marcar-todas-lidas")
    @Operation(summary = "Marcar todas como lidas", description = "Marca todas as notificações do usuário como lidas")
    public ResponseEntity<?> marcarTodasComoLidas(
            @AuthenticationPrincipal Usuario usuario) {
        
        List<Notificacao> naoLidas = notificacaoRepository
                .findByUsuarioAndLidaFalseOrderByDataEnvioDesc(usuario);
        
        naoLidas.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(naoLidas);
        
        return ResponseEntity.ok()
                .body(String.format("%d notificações marcadas como lidas", naoLidas.size()));
    }
    
    /**
     * Busca notificação por código de resgate
     */
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar por código", description = "Busca notificação pelo código de resgate (cupom)")
    public ResponseEntity<NotificacaoDTO> buscarPorCodigo(
            @PathVariable String codigo,
            @AuthenticationPrincipal Usuario usuario) {
        
        Notificacao notificacao = notificacaoRepository
                .findByUsuarioAndCodigoReferencia(usuario, codigo)
                .orElse(null);
        
        if (notificacao == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(converterParaDTO(notificacao));
    }
    
    /**
     * Conta quantas notificações não lidas o usuário tem
     */
    @GetMapping("/contar-nao-lidas")
    @Operation(summary = "Contar não lidas", description = "Retorna a quantidade de notificações não lidas")
    public ResponseEntity<Long> contarNaoLidas(
            @AuthenticationPrincipal Usuario usuario) {
        
        Long count = notificacaoRepository.countByUsuarioAndLidaFalse(usuario);
        return ResponseEntity.ok(count);
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private NotificacaoDTO converterParaDTO(Notificacao notificacao) {
        NotificacaoDTO dto = new NotificacaoDTO();
        dto.setId(notificacao.getId());
        dto.setTipo(notificacao.getTipo());
        dto.setAssunto(notificacao.getAssunto());
        dto.setCorpo(notificacao.getCorpo());
        dto.setDataEnvio(notificacao.getDataEnvio());
        dto.setLida(notificacao.getLida());
        dto.setCodigoReferencia(notificacao.getCodigoReferencia());
        return dto;
    }
}
