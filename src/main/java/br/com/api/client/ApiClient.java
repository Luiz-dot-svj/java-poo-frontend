package br.com.api.client;

import br.com.common.service.ApiServiceException;
import br.com.common.service.LocalDateAdapter;
import br.com.common.service.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private final OkHttpClient client;
    private final Gson gson;

    public ApiClient() {
        this.client = new OkHttpClient.Builder().build();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public <T> T get(String endpoint, Class<T> responseType) throws IOException, ApiServiceException {
        Request request = new Request.Builder().url(BASE_URL + endpoint).build();
        return execute(request, responseType);
    }

    public <T> T get(String endpoint, Type responseType) throws IOException, ApiServiceException {
        Request request = new Request.Builder().url(BASE_URL + endpoint).build();
        return execute(request, responseType);
    }

    public <T> T post(String endpoint, Object body, Class<T> responseType) throws IOException, ApiServiceException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(BASE_URL + endpoint).post(requestBody).build();
        return execute(request, responseType);
    }

    public <T> T put(String endpoint, Object body, Class<T> responseType) throws IOException, ApiServiceException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(BASE_URL + endpoint).put(requestBody).build();
        return execute(request, responseType);
    }

    private <T> T execute(Request request, Type responseType) throws IOException, ApiServiceException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new ApiServiceException("Erro na API: " + response.code() + " " + response.message() + " - Detalhes: " + errorBody);
            }
            String responseBody = response.body().string();
            if (responseType.equals(Void.class)) {
                return null;
            }
            return gson.fromJson(responseBody, responseType);
        }
    }
}
