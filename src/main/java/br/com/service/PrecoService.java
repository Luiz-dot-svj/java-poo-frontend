package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.PageResponse;
import br.com.api.dto.PrecoRequest;
import br.com.api.dto.PrecoResponse;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

public class PrecoService {

    private final ApiClient apiClient;

    public PrecoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<PrecoResponse> listPrecos(int page, int size) throws IOException, ApiServiceException {
        return apiClient.get("/precos?page=" + page + "&size=" + size, new TypeToken<PageResponse<PrecoResponse>>() {}.getType());
    }

    public PrecoResponse createPreco(PrecoRequest precoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/precos", precoRequest, PrecoResponse.class);
    }
}
