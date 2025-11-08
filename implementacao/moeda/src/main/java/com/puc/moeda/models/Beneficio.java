package com.puc.moeda.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
  
  @Column(columnDefinition = "LONGTEXT")
  private String foto; // Base64 ou URL da foto
  
  @ManyToOne
  @JoinColumn(name = "empresa_parceira_id", nullable = false)
  @JsonIgnoreProperties({"beneficios", "senha", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "username"})
  private EmpresaParceira empresaParceira;
  
  @Column(nullable = false)
  private Boolean ativo = true;

}
