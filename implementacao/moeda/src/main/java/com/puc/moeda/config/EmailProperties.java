package com.puc.moeda.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações customizadas de email
 */
@Configuration
@ConfigurationProperties(prefix = "app.mail")
@Data
public class EmailProperties {
    
    /**
     * Email do remetente
     */
    private String from = "noreply@moedaestudantil.com";
    
    /**
     * Nome do remetente
     */
    private String fromName = "Sistema de Moeda Estudantil";
}
