package br.com.acesso.dto;

import br.com.acesso.enums.TipoAcesso;

public record AcessoRequest(
    String usuario,
    String senha,
    TipoAcesso tipoAcesso
) {}
