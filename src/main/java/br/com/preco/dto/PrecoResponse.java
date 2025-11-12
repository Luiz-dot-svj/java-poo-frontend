package br.com.preco.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrecoResponse(
    Long id,
    BigDecimal valor,
    LocalDate dataAlteracao,
    LocalDate horaAlteracao,
    Long produtoId
) {}
