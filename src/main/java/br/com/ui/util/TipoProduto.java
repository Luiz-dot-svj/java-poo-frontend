package br.com.ui.util;

public enum TipoProduto {
    COMBUSTIVEL("Produto Combustível"),
    LUBRIFICANTE("Produto Lubrificante");

    private final String descricao;

    private TipoProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
