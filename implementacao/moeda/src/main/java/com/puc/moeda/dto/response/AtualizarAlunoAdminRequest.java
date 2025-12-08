package com.puc.moeda.dto.response;

public record AtualizarAlunoAdminRequest(
        String nome,
        String cpf,
        String rg,
        String endereco,
        String curso,
        String instituicao,
        Double saldoMoedas
) {}
