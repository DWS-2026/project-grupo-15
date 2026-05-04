package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;

public record ClassDTO(
    Long id,
    String name,
    String description,
    String schedule,
    String imageUrl
) {
    public ClassDTO(ClassEntity classEntity) {
        this(classEntity.getId(), 
             classEntity.getName(), 
             classEntity.getDescription(), 
             classEntity.getSchedule(), 
             classEntity.getImageUrl());
    }
}