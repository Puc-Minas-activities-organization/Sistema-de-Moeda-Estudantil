package com.puc.moeda.services;

import com.puc.moeda.dto.NotificacaoDTO;
import com.puc.moeda.models.Notificacao;
import com.puc.moeda.models.Usuario;
import com.puc.moeda.repositories.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar notificações/histórico de emails dos usuários
 */
@Service
public class HistoricoNotificacaoService {
    
    @Autowired
    private NotificacaoRepository notificacaoRepository;
    
    /**
     * Listar todas as notificações do usuário (ordenadas por data decrescente)
     */
    public List<NotificacaoDTO> listarNotificacoes(Usuario usuario) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioOrderByDataEnvioDesc(usuario);
        return notificacoes.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Listar apenas notificações não lidas
     */
    public List<NotificacaoDTO> listarNotificacoesNaoLidas(Usuario usuario) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataEnvioDesc(usuario);
        return notificacoes.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Contar notificações não lidas
     */
    public Long contarNaoLidas(Usuario usuario) {
        return notificacaoRepository.countByUsuarioAndLidaFalse(usuario);
    }
    
    /**
     * Marcar notificação como lida
     */
    @Transactional
    public void marcarComoLida(Long notificacaoId) {
        notificacaoRepository.findById(notificacaoId).ifPresent(notificacao -> {
            notificacao.setLida(true);
            notificacaoRepository.save(notificacao);
        });
    }
    
    /**
     * Marcar todas as notificações do usuário como lidas
     */
    @Transactional
    public void marcarTodasComoLidas(Usuario usuario) {
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataEnvioDesc(usuario);
        naoLidas.forEach(notificacao -> {
            notificacao.setLida(true);
            notificacaoRepository.save(notificacao);
        });
    }
    
    /**
     * Converter entidade Notificacao para DTO
     */
    private NotificacaoDTO convertToDTO(Notificacao notificacao) {
        return new NotificacaoDTO(
            notificacao.getId(),
            notificacao.getTipo(),
            notificacao.getAssunto(),
            notificacao.getCorpo(),
            notificacao.getDataEnvio(),
            notificacao.getLida(),
            notificacao.getCodigoReferencia()
        );
    }
}
