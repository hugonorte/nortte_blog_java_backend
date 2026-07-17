package com_abertamente_cms.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com_abertamente_cms.domain.ContentFormat;

public record PostContentUpdateRequest(
    @NotBlank(message = "O conteúdo não pode estar vazio")
    String content,

    @NotNull(message = "O formato de conteúdo é obrigatório")
    ContentFormat formatType
) {}
