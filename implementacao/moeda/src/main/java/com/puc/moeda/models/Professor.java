package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Professor extends Usuario {

  @Column(unique = true)
  private String cpf;

  private String nome;

  private String departamento;

  private String instituicao;

  private double saldoMoedas;
}
