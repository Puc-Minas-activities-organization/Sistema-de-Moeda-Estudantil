package com.puc.moeda.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Representa uma transação de envio de moedas de um professor para um aluno
 */
@Entity
@Data
@Table(name = "transacoes_moeda")
public class TransacaoMoeda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor remetente;
    
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno destinatario;
    
    @Column(nullable = false)
    private Double valor;
    
    @Column(nullable = false, length = 500)
    private String mensagem; // Motivo do reconhecimento
    
    @Column(nullable = false)
    private LocalDateTime dataTransacao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;
    
    @PrePersist
    protected void onCreate() {
        dataTransacao = LocalDateTime.now();
        if (status == null) {
            status = StatusTransacao.CONCLUIDA;
        }
    }
}
