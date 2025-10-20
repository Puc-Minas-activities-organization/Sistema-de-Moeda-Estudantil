package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Aluno extends Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String cpf;

  private String rg;
  private String instituicao;
  private double saldoMoedas;
  private String curso;
  private String endereco;
}
