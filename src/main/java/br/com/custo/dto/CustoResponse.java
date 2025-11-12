package br.com.custo.dto;

import br.com.custo.enums.TipoCusto;
import java.time.LocalDate;

public record CustoResponse(
    Long id,
    double imposto,
    double custoVariavel,
    double custoFixo,
    double margemLucro,
    LocalDate dataProcessamento,
    TipoCusto tipoCusto
) {}
