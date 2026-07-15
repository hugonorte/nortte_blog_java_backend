package com_abertamente_cms.dto.post;

import com_abertamente_cms.domain.Footnote;
import java.util.UUID;

public record FootnoteResponse(
        UUID id,
        String description
) {
    public static FootnoteResponse fromEntity(Footnote footnote) {
        return new FootnoteResponse(
                footnote.getId(),
                footnote.getDescription()
        );
    }
}
