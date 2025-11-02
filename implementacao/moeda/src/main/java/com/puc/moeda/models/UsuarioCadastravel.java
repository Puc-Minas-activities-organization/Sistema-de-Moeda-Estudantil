package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe abstrata para usuários que podem se auto-cadastrar no sistema.
 * Usuários que herdam desta classe: Aluno, EmpresaParceira
 * Usuários que NÃO herdam: Professor (só pode ser cadastrado por admin)
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UsuarioCadastravel extends Usuario {
    
    /**
     * Método para verificar se este tipo de usuário pode se auto-cadastrar.
     * Esta verificação é delegada ao enum Role.
     */
    public boolean podeSeAutoCadastrar() {
        return this.getRole() != null && this.getRole().podeSeAutoCadastrar();
    }
}
