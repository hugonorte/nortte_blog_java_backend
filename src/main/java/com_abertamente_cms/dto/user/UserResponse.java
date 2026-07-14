package com_abertamente_cms.dto.user;

import com_abertamente_cms.domain.Role;
import com_abertamente_cms.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String avatarPath,
        String role,
        String roleLabel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse fromEntity(User user) {
        String role = user.getRoles().stream().findFirst().map(Role::getName).orElse("");
        String roleLabel = user.getRoles().stream().findFirst().map(Role::getName).orElse("");

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarPath(),
                role,
                roleLabel,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
