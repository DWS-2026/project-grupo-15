package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.User;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String profileImageUrl) {

    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfileImageUrl());
    }
}