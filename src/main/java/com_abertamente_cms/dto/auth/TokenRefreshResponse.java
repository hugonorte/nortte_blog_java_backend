package com_abertamente_cms.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TokenRefreshResponse(
        String accessToken,
        @JsonIgnore
        String refreshToken,
        String tokenType
) {
    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
