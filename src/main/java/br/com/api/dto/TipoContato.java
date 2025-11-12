package br.com.api.dto;

public enum TipoContato {
    TELEFONE("Contato Telefone"),
    CELULAR("Contato Celular"),
    EMAIL("Contato e-mail");

    private final String descricao;

    private TipoContato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
