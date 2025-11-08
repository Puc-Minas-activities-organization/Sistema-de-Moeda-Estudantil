package com.puc.moeda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para retornar notificações/emails ao usuário
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoDTO {
    private Long id;
    private String tipo; // RECEBIMENTO_MOEDAS, RESGATE_BENEFICIO, RESGATE_EMPRESA
    private String assunto;
    private String corpo;
    private LocalDateTime dataEnvio;
    private Boolean lida;
    private String codigoReferencia; // Código de resgate para rastreamento
}
