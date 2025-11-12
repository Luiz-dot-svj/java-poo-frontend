package br.com.acesso.dto;

import br.com.acesso.enums.TipoAcesso;

public record AcessoResponse(
    Long id,
    String usuario,
    TipoAcesso tipoAcesso
) {}
