package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;

public record ClassResponseDTO(
    Long id,
    String name,
    String description,
    String schedule,
    ImageResponseDTO image // Usamos el DTO de imagen
) {
    public ClassResponseDTO(ClassEntity entity) {
        this(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getSchedule(),
            entity.getImage() != null ? new ImageResponseDTO(entity.getImage()) : null
        );
    }
}