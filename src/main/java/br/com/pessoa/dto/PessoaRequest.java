package br.com.pessoa.dto;

import br.com.pessoa.enums.TipoPessoa;
import java.time.LocalDate;

public record PessoaRequest(
    String nomeCompleto,
    String cpfCnpj,
    Long numeroCtps,
    LocalDate dataNascimento,
    TipoPessoa tipoPessoa
) {}
