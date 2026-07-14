package com_abertamente_cms.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PostRequest(
        @NotBlank(message = "O título não pode estar vazio")
        @Size(max = 255)
        String title,

        @NotBlank(message = "O slug não pode estar vazio")
        @Size(max = 255)
        String slug,

        String content,

        String tldr,

        String imagePath,

        java.time.Instant publishedAt,

        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId
) {
}
