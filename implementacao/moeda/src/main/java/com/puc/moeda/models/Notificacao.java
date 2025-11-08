package com.puc.moeda.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Representa uma notificação/email enviado para o usuário
 */
@Entity
@Data
@Table(name = "notificacoes", indexes = {
    @Index(name = "idx_usuario", columnList = "usuario_id"),
    @Index(name = "idx_data", columnList = "data_envio DESC")
})
public class Notificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"senha", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "username"})
    private Usuario usuario;
    
    @Column(nullable = false)
    private String tipo; // RECEBIMENTO_MOEDAS, RESGATE_BENEFICIO, RESGATE_EMPRESA
    
    @Column(nullable = false)
    private String assunto;
    
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String corpo;
    
    @Column(nullable = false)
    private LocalDateTime dataEnvio;
    
    @Column(nullable = false)
    private Boolean lida = false;
    
    private String codigoReferencia; // Código de resgate ou ID da transação para rastreamento
    
    @PrePersist
    protected void onCreate() {
        if (dataEnvio == null) {
            dataEnvio = LocalDateTime.now();
        }
    }
}
