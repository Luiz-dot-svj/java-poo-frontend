package br.com.preco.dto;

import br.com.preco.enums.TipoPreco;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PrecoRequest(
    BigDecimal valor,
    LocalDate dataAlteracao,
    LocalDate horaAlteracao,
    Long produtoId
) {}
