package com_abertamente_cms.dto.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record UserDto(
        UUID id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String email,
        String role
) {}
