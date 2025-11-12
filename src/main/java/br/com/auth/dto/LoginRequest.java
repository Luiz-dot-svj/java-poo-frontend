package br.com.auth.dto;

import com.google.gson.annotations.SerializedName;

public record LoginRequest(
        @SerializedName("usuario") String username,
        @SerializedName("senha") String password
) {
}
