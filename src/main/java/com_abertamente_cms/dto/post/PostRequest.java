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

        @NotNull(message = "O formato de conteúdo é obrigatório")
        com_abertamente_cms.domain.ContentFormat formatType,

        String tldr,

        String imagePath,

        java.time.Instant publishedAt,

        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId,
        
        @NotNull(message = "O autor é obrigatório")
        UUID authorId
) {
}
