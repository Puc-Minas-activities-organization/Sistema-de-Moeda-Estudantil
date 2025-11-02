package com.puc.moeda.dto;

import com.puc.moeda.models.StatusResgate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResgateResponse {
    private Long id;
    private String codigoResgate;
    private String nomeBeneficio;
    private Double valorPago;
    private LocalDateTime dataResgate;
    private StatusResgate status;
    private String mensagem;
}
