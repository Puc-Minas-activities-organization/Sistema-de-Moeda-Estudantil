package com.puc.moeda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficioRequest {
    private String descricao;
    private Double custo; // Custo em moedas
    private String foto; // URL ou base64 da foto
    private String nome;
}
