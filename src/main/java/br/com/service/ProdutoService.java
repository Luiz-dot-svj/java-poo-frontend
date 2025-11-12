package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.PageResponse;
import br.com.api.dto.ProdutoDTO;
import br.com.api.dto.ProdutoRequest;
import br.com.api.dto.ProdutoResponse;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

public class ProdutoService {

    private final ApiClient apiClient;

    public ProdutoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<ProdutoResponse> listProdutos(int page, int size) throws IOException, ApiServiceException {
        return apiClient.get("/produtos?page=" + page + "&size=" + size, new TypeToken<PageResponse<ProdutoResponse>>() {}.getType());
    }

    public ProdutoResponse createProduto(ProdutoRequest produtoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/produtos", produtoRequest, ProdutoResponse.class);
    }

    public ProdutoResponse getProdutoPorReferencia(String referencia) throws IOException, ApiServiceException {
        return apiClient.get("/produtos?referencia=" + referencia, ProdutoResponse.class);
    }

    public List<ProdutoDTO> buscarTodos() throws IOException, ApiServiceException {
        return apiClient.get("/produtos/all", new TypeToken<List<ProdutoDTO>>() {}.getType());
    }
}
