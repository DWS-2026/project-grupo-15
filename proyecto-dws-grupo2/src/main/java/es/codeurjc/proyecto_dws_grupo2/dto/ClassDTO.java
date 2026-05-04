package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;

public record ClassDTO(
    Long id,
    String name,
    String description,
    String schedule,
    String imageUrl
) {
    // 1. De Entidad a DTO (Se usa en los GET)
    public ClassDTO(ClassEntity classEntity) {
        this(classEntity.getId(), 
             classEntity.getName(), 
             classEntity.getDescription(), 
             classEntity.getSchedule(), 
             classEntity.getImageUrl());
    }

    // 2. De DTO a nueva Entidad (Se usa en el POST)
    public ClassEntity toEntity() {
        ClassEntity entity = new ClassEntity(this.name, this.description, this.schedule);
        entity.setImageUrl(this.imageUrl);
        return entity;
    }

    // 3. Actualizar Entidad existente con datos del DTO (Se usa en el PUT)
    public void updateEntity(ClassEntity entity) {
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setSchedule(this.schedule);
        entity.setImageUrl(this.imageUrl);
    }
}