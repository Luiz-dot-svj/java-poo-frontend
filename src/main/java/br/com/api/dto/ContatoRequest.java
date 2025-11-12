package br.com.api.dto;

public record ContatoRequest(
        String telefone,
        String email,
        String endereco,
        TipoContato tipoContato
)
{ }
