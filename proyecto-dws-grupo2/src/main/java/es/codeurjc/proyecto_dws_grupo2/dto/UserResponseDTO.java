package es.codeurjc.proyecto_dws_grupo2.dto;

import java.util.List;
import es.codeurjc.proyecto_dws_grupo2.model.User;

public record UserResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String email,
    List<String> roles,
    ImageResponseDTO profileImage
) {
    public UserResponseDTO(User user) {
        this(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRoles(),
            // If the user haves image, we create DTO. If not, return null
            user.getProfileImage() != null ? new ImageResponseDTO(user.getProfileImage()) : null
        );
    }
}