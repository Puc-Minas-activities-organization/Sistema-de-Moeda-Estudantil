package com.puc.moeda.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtratoItemDTO {
  private String tipo; // "RECEBIMENTO" ou "ENVIO" ou "RESGATE"
  private Double valor;
  private String descricao;
  private LocalDateTime data;
  private Double saldoAposTransacao;

  // Dados extras para frontend
  private String professorNome;
  private String professorEmail;
  private String empresaNome;
  private String alunoNome;
  private String alunoEmail;
}
