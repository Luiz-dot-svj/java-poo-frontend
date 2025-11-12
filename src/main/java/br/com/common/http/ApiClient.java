package br.com.common.http;

import br.com.common.service.ApiServiceException;
import br.com.common.service.LocalDateAdapter;
import br.com.common.service.LocalDateTimeAdapter;
import br.com.common.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
        Request request = buildRequest(endpoint, "GET", null);
        return execute(request, responseType);
    }

    public <T> T get(String endpoint, Type responseType) throws IOException, ApiServiceException {
        Request request = buildRequest(endpoint, "GET", null);
        return execute(request, responseType);
    }

    public <T> T post(String endpoint, Object body, Class<T> responseType) throws IOException, ApiServiceException {
        Request request = buildRequest(endpoint, "POST", body);
        return execute(request, responseType);
    }

    public <T> T put(String endpoint, Object body, Class<T> responseType) throws IOException, ApiServiceException {
        Request request = buildRequest(endpoint, "PUT", body);
        return execute(request, responseType);
    }

    public void delete(String endpoint) throws IOException, ApiServiceException {
        Request request = buildRequest(endpoint, "DELETE", null);
        execute(request);
    }

    private Request buildRequest(String endpoint, String method, Object body) {
        String url = BASE_URL + endpoint;
        Request.Builder builder = new Request.Builder().url(url);

        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        if (body != null) {
            String jsonBody = gson.toJson(body);
            RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            builder.method(method, requestBody);
        } else {
            builder.method(method, null);
        }

        return builder.build();
    }

    private <T> T execute(Request request, Type responseType) throws IOException, ApiServiceException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new ApiServiceException("Erro na API: " + response.code() + " " + response.message() + " - Detalhes: " + errorBody);
            }
            String responseBody = response.body().string();
            if (responseBody.isEmpty() && responseType == Void.class) {
                return null;
            }
            return gson.fromJson(responseBody, responseType);
        }
    }

    private void execute(Request request) throws IOException, ApiServiceException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new ApiServiceException("Erro na API: " + response.code() + " " + response.message() + " - Detalhes: " + errorBody);
            }
        }
    }
}
