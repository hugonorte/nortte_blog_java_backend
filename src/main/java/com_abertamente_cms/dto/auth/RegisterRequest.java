package com_abertamente_cms.dto.auth;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "O primeiro nome é obrigatório.")
        @JsonProperty("first_name") String firstName,
        
        @JsonProperty("last_name") String lastName,
        
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Forneça um e-mail válido.")
        String email,
        
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password
) {}
