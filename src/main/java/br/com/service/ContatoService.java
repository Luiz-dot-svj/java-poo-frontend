package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.ContatoRequest;
import br.com.api.dto.ContatoResponse;
import br.com.api.dto.PageResponse;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

public class ContatoService {

    private final ApiClient apiClient;

    public ContatoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<ContatoResponse> listContatos(int page, int size) throws IOException, ApiServiceException {
        return apiClient.get("/contatos?page=" + page + "&size=" + size, new TypeToken<PageResponse<ContatoResponse>>() {}.getType());
    }

    public ContatoResponse createContato(ContatoRequest contatoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/contatos", contatoRequest, ContatoResponse.class);
    }

    public ContatoResponse getContatoPorEmail(String email) throws IOException, ApiServiceException {
        return apiClient.get("/contatos?email=" + email, ContatoResponse.class);
    }
}
