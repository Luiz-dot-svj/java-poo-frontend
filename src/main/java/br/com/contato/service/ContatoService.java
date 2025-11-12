package br.com.contato.service;

import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.contato.dto.ContatoListResponse;
import br.com.contato.dto.ContatoRequest;
import br.com.contato.dto.ContatoResponse;

import java.io.IOException;
import java.util.List;

public class ContatoService {

    private final ApiClient apiClient;

    public ContatoService() {
        this.apiClient = new ApiClient();
    }

    public List<ContatoResponse> findContatos() throws IOException, ApiServiceException {
        ContatoListResponse contatoListResponse = apiClient.get("/contatos", ContatoListResponse.class);
        return contatoListResponse.getContatos();
    }

    public ContatoResponse findContatoById(Long id) throws IOException, ApiServiceException {
        return apiClient.get("/contatos/" + id, ContatoResponse.class);
    }

    public ContatoResponse createContato(ContatoRequest contatoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/contatos", contatoRequest, ContatoResponse.class);
    }

    public ContatoResponse updateContato(Long id, ContatoRequest contatoRequest) throws IOException, ApiServiceException {
        return apiClient.put("/contatos/" + id, contatoRequest, ContatoResponse.class);
    }

    public void deleteContato(Long id) throws IOException, ApiServiceException {
        apiClient.delete("/contatos/" + id);
    }
}
