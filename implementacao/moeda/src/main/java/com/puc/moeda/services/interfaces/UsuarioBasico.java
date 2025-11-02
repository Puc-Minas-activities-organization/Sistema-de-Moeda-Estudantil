package com.puc.moeda.services.interfaces;

import com.puc.moeda.models.Usuario;

/**
 * Interface para operações básicas de usuário (login, consulta, alteração)
 */
public interface UsuarioBasico<T extends Usuario, TRequest, TResponse, TPerfilResponse> {
    TResponse alterarDados(String email, TRequest request);
    TPerfilResponse consultarPerfil(String email);
    boolean validarCredenciais(String email, String senha);
}