package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.Review;

public record ReviewResponseDTO(
    Long id,
    String about,
    int rating,
    String comment,
    UserResponseDTO user
) {
    public ReviewResponseDTO(Review review) {
        this(
            review.getId(),
            review.getAbout(),
            review.getRating(),
            review.getComment(),
            review.getUser() != null ? new UserResponseDTO(review.getUser()) : null
        );
    }
}