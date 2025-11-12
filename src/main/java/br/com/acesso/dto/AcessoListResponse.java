package br.com.acesso.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AcessoListResponse {

    @SerializedName("content")
    private List<AcessoResponse> acessos;

    public List<AcessoResponse> getAcessos() {
        return acessos;
    }

    public void setAcessos(List<AcessoResponse> acessos) {
        this.acessos = acessos;
    }
}
