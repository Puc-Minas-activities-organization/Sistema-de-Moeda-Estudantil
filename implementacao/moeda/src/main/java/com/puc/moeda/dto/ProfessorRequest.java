package com.puc.moeda.dto;

import lombok.Data;

@Data
public class ProfessorRequest {
    private String email;
    private String senha;
    private String cpf;
    private String nome;
    private String departamento;
    private String instituicao;
}