package br.com.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EstoqueRequest(
        BigDecimal quantidade,
        String localTannque,
        String localEndereco,
        String localFabricacao,
        LocalDate dataValidade,
        TipoEstoque tipoEstoque

        ) {
}
