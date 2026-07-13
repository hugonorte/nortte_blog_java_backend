package com_abertamente_cms.dto.user;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email,
        String role
) {}
