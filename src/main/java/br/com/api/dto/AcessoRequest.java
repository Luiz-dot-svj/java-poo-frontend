package br.com.api.dto;

public record AcessoRequest(
        String usuario,
        String senha,
        TipoAcesso tipoAcesso
)
{ }
