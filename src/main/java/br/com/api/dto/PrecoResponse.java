package br.com.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrecoResponse(
        LocalDate dataAlteracao,
        LocalDate horaAlteracao,
        BigDecimal valor
) {

}
