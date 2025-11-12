package br.com.api.dto;

import java.util.List;

public class ProdutoDTO {

    private String nome;
    private String tipoProduto;
    private List<PrecoDTO> precos;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public List<PrecoDTO> getPrecos() {
        return precos;
    }

    public void setPrecos(List<PrecoDTO> precos) {
        this.precos = precos;
    }
}
