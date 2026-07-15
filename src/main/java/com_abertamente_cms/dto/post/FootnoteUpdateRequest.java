package com_abertamente_cms.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FootnoteUpdateRequest(
        @NotNull(message = "O ID não pode estar vazio")
        UUID id,
        @NotBlank(message = "A descrição não pode estar vazia")
        String description
) {
}
