package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.Image;

public record ImageResponseDTO(
    Long id,
    String url
) {
    public ImageResponseDTO(Image image) {
        this(
            image.getId(),
            "/api/v1/images/" + image.getId() + "/media"
        );
    }
}