package br.com.ui.model;

import br.com.ui.util.TipoProduto;

public class Produto {
    private Long id;
    private String nome;
    private String referencia;
    private String fornecedor;
    private String marca;
    private String categoria;
    private TipoProduto tipoProduto;

    public Produto() {
    }

    public Produto(Long id, String nome, String referencia, String fornecedor, String marca, String categoria, TipoProduto tipoProduto) {
        this.id = id;
        this.nome = nome;
        this.referencia = referencia;
        this.fornecedor = fornecedor;
        this.marca = marca;
        this.categoria = categoria;
        this.tipoProduto = tipoProduto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    @Override
    public String toString() {
        return "Produto{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", referencia='" + referencia + '\'' +
               ", fornecedor='" + fornecedor + '\'' +
               ", marca='" + marca + '\'' +
               ", categoria='" + categoria + '\'' +
               ", tipoProduto=" + tipoProduto +
               '}';
    }
}
