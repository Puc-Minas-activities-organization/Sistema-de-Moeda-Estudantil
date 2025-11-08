package com.puc.moeda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de cadastro/edição de benefício
 * 
 * O campo 'foto' aceita:
 * 1. URL completa: "https://example.com/foto.jpg"
 * 2. Data URI Base64: "data:image/png;base64,iVBORw0KGgo..."
 * 3. Base64 puro: "iVBORw0KGgo..." (será convertido para Data URI)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficioRequest {
    private String nome;
    private Double custo; // Custo em moedas
    private String descricao;
    private String foto; // Base64 (Data URI ou puro) ou URL
}

