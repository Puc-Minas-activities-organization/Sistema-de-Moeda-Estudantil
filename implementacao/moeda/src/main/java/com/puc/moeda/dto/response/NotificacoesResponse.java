package com.puc.moeda.dto.response;

import java.util.List;

import com.puc.moeda.dto.NotificacaoDTO;

public record NotificacoesResponse(List<NotificacaoDTO> notificacoes, Long naoLidas) {}
