package com.puc.moeda.models;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class EmpresaParceira extends Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String cnpj;

  @OneToMany(mappedBy = "empresaParceira", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Beneficio> beneficios;
}
