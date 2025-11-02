package com.puc.moeda.dto;

import lombok.Data;

@Data
public class AlunoRequest {
    private String email;
    private String senha;
    private String cpf;
    private String rg;
    private String nome;
    private String instituicao;
    private String curso;
    private String endereco;
}