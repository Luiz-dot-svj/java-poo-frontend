package br.com.contato.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContatoListResponse {

    @SerializedName("content")
    private List<ContatoResponse> contatos;

    public List<ContatoResponse> getContatos() {
        return contatos;
    }

    public void setContatos(List<ContatoResponse> contatos) {
        this.contatos = contatos;
    }
}
