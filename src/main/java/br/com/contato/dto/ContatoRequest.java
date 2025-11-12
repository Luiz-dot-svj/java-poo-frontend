package br.com.contato.dto;

import br.com.contato.enums.TipoContato;

public record ContatoRequest(
    String telefone,
    String email,
    String endereco,
    TipoContato tipoContato,
    Long pessoaId
) {}
