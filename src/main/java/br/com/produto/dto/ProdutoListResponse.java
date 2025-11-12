package br.com.produto.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProdutoListResponse {

    @SerializedName("content")
    private List<ProdutoResponse> produtos;

    public List<ProdutoResponse> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoResponse> produtos) {
        this.produtos = produtos;
    }
}
