package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;

public record ClassRequestDTO(
    String name,
    String description,
    String schedule
) {
     // 2. From DTO to new entity
    public ClassEntity toEntity() {
        return new ClassEntity(this.name, this.description, this.schedule);
    }

     // 3. Update entity with data from DTO
    public void updateEntity(ClassEntity entity) {
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setSchedule(this.schedule);
    }
}