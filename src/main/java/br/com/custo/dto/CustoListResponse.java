package br.com.custo.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CustoListResponse {

    @SerializedName("content")
    private List<CustoResponse> custos;

    public List<CustoResponse> getCustos() {
        return custos;
    }

    public void setCustos(List<CustoResponse> custos) {
        this.custos = custos;
    }
}
