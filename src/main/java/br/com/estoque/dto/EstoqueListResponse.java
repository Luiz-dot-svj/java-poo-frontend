package br.com.estoque.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EstoqueListResponse {

    @SerializedName("content")
    private List<EstoqueResponse> estoques;

    public List<EstoqueResponse> getEstoques() {
        return estoques;
    }

    public void setEstoques(List<EstoqueResponse> estoques) {
        this.estoques = estoques;
    }
}
