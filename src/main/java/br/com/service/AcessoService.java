package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.AcessoRequest;
import br.com.api.dto.AcessoResponse;
import br.com.api.dto.PageResponse;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

public class AcessoService {

    private final ApiClient apiClient;

    public AcessoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<AcessoResponse> listAcessos(int page, int size) throws IOException, ApiServiceException {
        return apiClient.get("/acessos?page=" + page + "&size=" + size, new TypeToken<PageResponse<AcessoResponse>>() {}.getType());
    }

    public AcessoResponse createAcesso(AcessoRequest acessoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/acessos", acessoRequest, AcessoResponse.class);
    }

    public AcessoResponse login(String usuario, String senha) throws IOException, ApiServiceException {
        AcessoRequest request = new AcessoRequest(usuario, senha, null);
        return apiClient.post("/acessos", request, AcessoResponse.class);
    }

    public AcessoResponse getAcessoPorUsuario(String usuario) throws IOException, ApiServiceException {
        return apiClient.get("/acessos?usuario=" + usuario, AcessoResponse.class);
    }
}
