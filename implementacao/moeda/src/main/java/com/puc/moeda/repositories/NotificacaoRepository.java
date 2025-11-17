package com.puc.moeda.repositories;

import com.puc.moeda.models.Notificacao;
import com.puc.moeda.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    
    /**
     * Busca todas as notificações de um usuário, ordenadas por data (mais recentes primeiro)
     */
    List<Notificacao> findByUsuarioOrderByDataEnvioDesc(Usuario usuario);
    
    /**
     * Busca todas as notificações NÃO LIDAS de um usuário
     */
    List<Notificacao> findByUsuarioAndLidaFalseOrderByDataEnvioDesc(Usuario usuario);
    
    /**
     * Conta notificações não lidas
     */
    Long countByUsuarioAndLidaFalse(Usuario usuario);
    
    /**
     * Busca notificação por usuário e código de referência (código de resgate)
     */
    Optional<Notificacao> findByUsuarioAndCodigoReferencia(Usuario usuario, String codigoReferencia);
}
