package com_abertamente_cms.dto.post;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FootnoteRequest(
        @NotNull(message = "O postId não pode estar vazio")
        UUID post_id,
        @NotBlank(message = "A descrição não pode estar vazia")
        String description
) {
}
