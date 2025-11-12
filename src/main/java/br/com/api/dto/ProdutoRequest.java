package br.com.api.dto;

public record ProdutoRequest(
        String nome,
        String referencia,
        String fornecedor,
        String marca,
        String categoria,
        TipoProduto tipoProduto
) {
}
