package com_abertamente_cms.dto.author;

import com_abertamente_cms.domain.Author;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record AuthorResponse(
        UUID id,
        
        String name,
        
        String email,
        
        String bio,
        
        @JsonProperty("main_title")
        String mainTitle,
        
        @JsonProperty("preferred_social_network")
        String preferredSocialNetwork,
        
        @JsonProperty("preferred_social_network_username")
        String preferredSocialNetworkUsername
) {
    public static AuthorResponse fromEntity(Author author) {
        if (author == null) return null;
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getBio(),
                author.getMainTitle(),
                author.getPreferredSocialNetwork(),
                author.getPreferredSocialNetworkUsername()
        );
    }
}
