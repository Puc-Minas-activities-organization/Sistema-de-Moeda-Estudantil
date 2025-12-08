package com.puc.moeda.dto.response;

import java.util.List;

import com.puc.moeda.dto.ExtratoItemDTO;

public record ExtratoResponse(Double saldoAtual, List<ExtratoItemDTO> transacoes) {}
    