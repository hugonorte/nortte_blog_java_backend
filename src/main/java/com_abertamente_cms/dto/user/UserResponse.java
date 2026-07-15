package com_abertamente_cms.dto.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.domain.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String email,
        String avatarPath,
        String role,
        String roleLabel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse fromEntity(User user) {
        String roleStr = user.getRole() != null ? user.getRole().name() : "";
        String roleLabelStr = user.getRole() != null ? user.getRole().getDescription() : "";

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarPath(),
                roleStr,
                roleLabelStr,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
