package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Aluno extends UsuarioCadastravel {

  @Column(unique = true)
  private String cpf;

  private String nome;
  private String rg;
  private String instituicao;
  private double saldoMoedas;
  private String curso;
  private String endereco;
}
