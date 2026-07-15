package com_abertamente_cms.dto.auth;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthResponse(
        String token,
        @JsonIgnore
        String refreshToken,
        UUID id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String email,
        String role
) {}
