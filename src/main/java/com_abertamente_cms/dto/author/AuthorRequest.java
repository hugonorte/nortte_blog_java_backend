package com_abertamente_cms.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255)
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "A biografia é obrigatória")
        String bio,

        @NotBlank(message = "O título principal é obrigatório")
        @Size(max = 255, message = "O título principal deve ter no máximo 255 caracteres")
        @JsonProperty("main_title")
        String mainTitle,

        @Size(max = 255, message = "A rede social preferida deve ter no máximo 255 caracteres")
        @JsonProperty("preferred_social_network")
        String preferredSocialNetwork,

        @Size(max = 255, message = "O nome de usuário da rede social deve ter no máximo 255 caracteres")
        @JsonProperty("preferred_social_network_username")
        String preferredSocialNetworkUsername
) {}
