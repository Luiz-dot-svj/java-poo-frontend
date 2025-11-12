package br.com.auth.dto;

import br.com.acesso.enums.TipoAcesso;

public record LoginResponse(String token, TipoAcesso tipoAcesso) {
}
