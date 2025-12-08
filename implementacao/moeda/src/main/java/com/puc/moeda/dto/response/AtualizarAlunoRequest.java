package com.puc.moeda.dto.response;

public record AtualizarAlunoRequest(
        String nome,
        String endereco,
        String curso,
        String email,
        String senha
) {}
