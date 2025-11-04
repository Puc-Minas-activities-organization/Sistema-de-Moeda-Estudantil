package com.puc.moeda.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  private String endereco;

  @OneToMany(mappedBy = "empresaParceira", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Beneficio> beneficios;
}
