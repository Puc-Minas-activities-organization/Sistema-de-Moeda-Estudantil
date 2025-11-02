package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Beneficio {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String nome;
  
  @Column(nullable = false)
  private Double custo; // Custo em moedas
  
  @Column(length = 1000)
  private String descricao;
  
  @Column(length = 500)
  private String foto; // URL ou caminho da foto
  
  @ManyToOne
  @JoinColumn(name = "empresa_parceira_id", nullable = false)
  private EmpresaParceira empresaParceira;
  
  @Column(nullable = false)
  private Boolean ativo = true; // Se o benefício está disponível

}
