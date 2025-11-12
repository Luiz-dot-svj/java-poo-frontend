package br.com.api.dto;

import java.time.LocalDate;


public record CustoResponse(
        double imposto,
        double custoVariavel,
        double custoFixo,
        double margemLucro,
        LocalDate dataProcessamento
) {
}
