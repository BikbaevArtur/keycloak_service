package ru.bikbaev.keycloak_service.model.dto;

import lombok.NonNull;

public record UserRegistrationRecord(@NonNull String username, @NonNull String email,@NonNull String phone, @NonNull String password) {
}
