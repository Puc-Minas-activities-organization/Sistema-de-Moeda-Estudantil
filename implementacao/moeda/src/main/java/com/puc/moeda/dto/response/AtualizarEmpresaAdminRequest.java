package com.puc.moeda.dto.response;

public record AtualizarEmpresaAdminRequest(
        String nome,
        String cnpj
) {}