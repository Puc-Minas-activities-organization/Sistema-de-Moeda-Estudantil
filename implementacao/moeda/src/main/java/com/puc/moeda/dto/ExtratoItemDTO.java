package com.puc.moeda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtratoItemDTO {
    private String tipo; // "RECEBIMENTO" ou "ENVIO" ou "RESGATE"
    private Double valor;
    private String descricao;
    private LocalDateTime data;
    private Double saldoAposTransacao;
}
