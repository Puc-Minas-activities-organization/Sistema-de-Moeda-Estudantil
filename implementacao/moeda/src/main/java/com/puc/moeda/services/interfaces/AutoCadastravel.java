package com.puc.moeda.services.interfaces;

import com.puc.moeda.models.Usuario;

/**
 * Interface para usuários que podem se auto-cadastrar no sistema
 */
public interface AutoCadastravel<T extends Usuario, TRequest, TResponse> {
    TResponse cadastrar(TRequest request);
    boolean validarDadosCadastro(TRequest request);
}