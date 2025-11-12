package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.PageResponse;
import br.com.api.dto.PessoaRequest;
import br.com.api.dto.PessoaResponse;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

public class PessoaService {

    private final ApiClient apiClient;

    public PessoaService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<PessoaResponse> listPessoas(int page, int size) throws IOException, ApiServiceException {
        return apiClient.get("/pessoas?page=" + page + "&size=" + size, new TypeToken<PageResponse<PessoaResponse>>() {}.getType());
    }

    public PessoaResponse createPessoa(PessoaRequest pessoaRequest) throws IOException, ApiServiceException {
        return apiClient.post("/pessoas", pessoaRequest, PessoaResponse.class);
    }

    public PessoaResponse getPessoaPorCpfCnpj(String cpfCnpj) throws IOException, ApiServiceException {
        return apiClient.get("/pessoas?cpfCnpj=" + cpfCnpj, PessoaResponse.class);
    }
}
