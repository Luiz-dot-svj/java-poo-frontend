package br.com.produto.dto;

import br.com.produto.enums.TipoProduto;

public record ProdutoRequest(
    String nome,
    String referencia,
    String fornecedor,
    String marca,
    String categoria,
    TipoProduto tipoProduto
) {}
