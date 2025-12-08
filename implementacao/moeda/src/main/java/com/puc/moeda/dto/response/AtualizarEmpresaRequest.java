package com.puc.moeda.dto.response;

public record AtualizarEmpresaRequest(
        String nome,
        String email,
        String senha,
        String endereco
) {}
