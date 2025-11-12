package br.com.api.dto;

public enum TipoCusto {
    ENERGIA("Custo de Enengia"),
    FRETE("Custo de Frete"),
    AGUA("Custo de Água");


    private final String descricao;

    private TipoCusto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
