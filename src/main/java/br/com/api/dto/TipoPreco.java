package br.com.api.dto;

public enum TipoPreco {
    UNITARIO("Preço Unitário"),
    TOTAL("Preço Total");

    private final String descricao;

    private TipoPreco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
