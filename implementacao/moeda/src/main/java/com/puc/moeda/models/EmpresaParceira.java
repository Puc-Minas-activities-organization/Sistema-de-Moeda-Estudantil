package com.puc.moeda.models;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class EmpresaParceira extends UsuarioCadastravel {

  @Column(unique = true)
  private String cnpj;

  private String nome;

  @OneToMany(mappedBy = "empresaParceira", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Beneficio> beneficios;
}
