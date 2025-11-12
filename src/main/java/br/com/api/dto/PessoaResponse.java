package br.com.api.dto;

import java.time.LocalDate;

public record PessoaResponse(
        String nomeCompleto,
        String cpfCnpj,
        long numeroCtps,
        LocalDate dataNascimento
){}
