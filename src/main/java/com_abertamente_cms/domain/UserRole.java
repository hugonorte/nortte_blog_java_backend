package com_abertamente_cms.domain;

public enum UserRole {
    ADMIN("Administrador"),
    EDITOR("Editor"),
    AUTHOR("Autor"),
    USER("Usuário");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
