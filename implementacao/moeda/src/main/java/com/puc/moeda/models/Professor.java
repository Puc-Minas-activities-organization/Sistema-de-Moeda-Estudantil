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

  public Professor() {}

  public Professor(
      String cpf, String nome, String departamento, String instituicao, double saldoMoedas) {
    this.cpf = cpf;
    this.nome = nome;
    this.departamento = departamento;
    this.instituicao = instituicao;
    this.saldoMoedas = saldoMoedas;
  }
}
