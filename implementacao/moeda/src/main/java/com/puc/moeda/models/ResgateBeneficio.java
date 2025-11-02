package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa o resgate de um benefício por um aluno
 */
@Entity
@Data
@Table(name = "resgates_beneficio")
public class ResgateBeneficio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;
    
    @ManyToOne
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficio beneficio;
    
    @Column(nullable = false, unique = true)
    private String codigoResgate; // Código único para conferência
    
    @Column(nullable = false)
    private Double valorPago; // Valor em moedas que foi pago
    
    @Column(nullable = false)
    private LocalDateTime dataResgate;
    
    private LocalDateTime dataUtilizacao; // Quando o benefício foi usado
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusResgate status;
    
    @PrePersist
    protected void onCreate() {
        dataResgate = LocalDateTime.now();
        codigoResgate = gerarCodigoResgate();
        if (status == null) {
            status = StatusResgate.PENDENTE;
        }
    }
    
    private String gerarCodigoResgate() {
        // Gera código único: RES-XXXXXX
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public void marcarComoUsado() {
        this.status = StatusResgate.USADO;
        this.dataUtilizacao = LocalDateTime.now();
    }
}
