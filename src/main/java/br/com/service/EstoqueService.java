package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.EstoqueRequest;
import br.com.api.dto.EstoqueResponse;
import br.com.api.dto.PageResponse;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

public class EstoqueService {

    private final ApiClient apiClient;

    public EstoqueService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<EstoqueResponse> listEstoques(int page, int size) throws IOException, ApiServiceException {
        return apiClient.get("/estoques?page=" + page + "&size=" + size, new TypeToken<PageResponse<EstoqueResponse>>() {}.getType());
    }

    public EstoqueResponse createEstoque(EstoqueRequest estoqueRequest) throws IOException, ApiServiceException {
        return apiClient.post("/estoques", estoqueRequest, EstoqueResponse.class);
    }

    public EstoqueResponse getEstoquePorLoteFabricacao(String loteFabricacao) throws IOException, ApiServiceException {
        return apiClient.get("/estoques?loteFabricacao=" + loteFabricacao, EstoqueResponse.class);
    }
}
