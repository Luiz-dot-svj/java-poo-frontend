package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.BombaDTO;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

public class BombaService {

    private final ApiClient apiClient;

    public BombaService() {
        this.apiClient = new ApiClient();
    }

    public List<BombaDTO> buscarTodas() throws IOException, ApiServiceException {
        return apiClient.get("/bombas", new TypeToken<List<BombaDTO>>() {}.getType());
    }

    public void atualizarStatus(Long id, String status) throws IOException, ApiServiceException {
        apiClient.put("/bombas/" + id + "/status", status, Void.class);
    }
}
