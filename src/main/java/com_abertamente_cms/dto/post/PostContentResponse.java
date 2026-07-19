package com_abertamente_cms.dto.post;

import com_abertamente_cms.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostContentResponse(
        UUID id,
        String title,
        String slug,
        String tldr,
        String content,
        String imagePath,
        @JsonProperty("author_id") UUID authorId,
        @JsonProperty("author_main_title") String authorMainTitle,
        @JsonProperty("author_preferred_social_network") String authorPreferredSocialNetwork,
        @JsonProperty("author_preferred_social_network_username") String authorPreferredSocialNetworkUsername,
        @JsonProperty("author_bio") String authorBio,
        @JsonProperty("category_id") UUID categoryId,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("author_name") String authorName,
        @JsonProperty("published_at") java.time.Instant publishedAt,
        String status,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        @JsonProperty("deleted_at") LocalDateTime deletedAt
) {
    public static PostContentResponse fromEntity(Post post, String processedHtml) {
        return new PostContentResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getTldr(),
                processedHtml,
                post.getImagePath(),
                post.getAuthor().getId(),
                post.getAuthor().getMainTitle(),
                post.getAuthor().getPreferredSocialNetwork(),
                post.getAuthor().getPreferredSocialNetworkUsername(),
                post.getAuthor().getBio(),
                post.getCategory().getId(),
                post.getCategory().getName(),
                post.getAuthor().getName(),
                post.getPublishedAt(),
                post.getStatus() != null ? post.getStatus().name() : null,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getDeletedAt()
        );
    }
}
