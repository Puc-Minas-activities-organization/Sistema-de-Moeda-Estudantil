package com.puc.moeda.models;

public enum Role {
    PROFESSOR,
    ALUNO,
    EMPRESA_PARCEIRA;
    
    public boolean podeSeAutoCadastrar() {
        return this == ALUNO || this == EMPRESA_PARCEIRA;
    }
    
    public boolean podeEnviarMoedas() {
        return this == PROFESSOR;
    }
    
    public boolean podeResgatarBeneficios() {
        return this == ALUNO;
    }
    
    public boolean podeGerenciarBeneficios() {
        return this == EMPRESA_PARCEIRA;
    }
}