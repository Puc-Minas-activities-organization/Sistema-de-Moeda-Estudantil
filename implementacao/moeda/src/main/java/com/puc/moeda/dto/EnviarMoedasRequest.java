package com.puc.moeda.dto;

import lombok.Data;

@Data
public class EnviarMoedasRequest {
    private Long alunoId;
    private Double valor;
    private String mensagem; // Motivo do reconhecimento
}
