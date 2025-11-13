package br.com.api.dto;

import br.com.acesso.enums.TipoAcesso; // Importa o TipoAcesso correto

public record AcessoRequest(
        String usuario,
        String senha,
        TipoAcesso tipoAcesso
)
{ }
