package br.com.preco.service;

import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.preco.dto.PrecoRequest;
import br.com.preco.dto.PrecoResponse;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PrecoService {

    private final ApiClient apiClient;

    public PrecoService() {
        this.apiClient = new ApiClient();
    }

    public List<PrecoResponse> findPrecos() throws IOException, ApiServiceException {
        Type responseType = new TypeToken<List<PrecoResponse>>() {}.getType();
        return apiClient.get("/precos", responseType);
    }

    public PrecoResponse findPrecoById(Long id) throws IOException, ApiServiceException {
        return apiClient.get("/precos/" + id, PrecoResponse.class);
    }

    public PrecoResponse createPreco(PrecoRequest precoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/precos", precoRequest, PrecoResponse.class);
    }

    public PrecoResponse updatePreco(Long id, PrecoRequest precoRequest) throws IOException, ApiServiceException {
        return apiClient.put("/precos/" + id, precoRequest, PrecoResponse.class);
    }
}
