package br.com.api.dto;

public record ContatoResponse(
        String telefone,
        String email,
        String endereco
) {

}
