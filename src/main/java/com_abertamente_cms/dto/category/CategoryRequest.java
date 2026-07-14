package com_abertamente_cms.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "O nome não pode estar vazio")
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String slug,

        String description
) {
}
