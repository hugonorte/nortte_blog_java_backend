package com_abertamente_cms.dto.post;

import com_abertamente_cms.domain.BibliographicReference;
import java.util.UUID;

public record BibliographicReferenceResponse(
        UUID id,
        String description
) {
    public static BibliographicReferenceResponse fromEntity(BibliographicReference reference) {
        return new BibliographicReferenceResponse(
                reference.getId(),
                reference.getDescription()
        );
    }
}
