package com.puc.moeda.dto;

import lombok.Data;

@Data
public class EmpresaParceiraRequest {
    private String email;
    private String senha;
    private String cnpj;
    private String nome;
    private String endereco;
}