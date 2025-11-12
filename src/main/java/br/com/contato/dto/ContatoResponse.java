package br.com.contato.dto;

import br.com.contato.enums.TipoContato;

public record ContatoResponse(
    Long id,
    String telefone,
    String email,
    String endereco,
    TipoContato tipoContato,
    Long pessoaId
) {}
