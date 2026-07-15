package com_abertamente_cms.dto.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record UserRequest(
        @NotBlank(message = "First name is required") @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
        String password,
        String role
) {}
