package com_abertamente_cms.dto.auth;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthResponse(
        String token,
        @JsonIgnore
        String refreshToken,
        UUID id,
        String name,
        String email,
        String role
) {}
