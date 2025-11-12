package br.com.api.dto;

public record ProdutoResponse(
        String nome,
        String referencia,
        String fornecedor,
        String marca,
        String categoria
) {
}
