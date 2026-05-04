package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;

public record ClassRequestDTO(
    String name,
    String description,
    String schedule
) {
     // 2. De DTO a nueva Entidad (Se usa en el POST)
    public ClassEntity toEntity() {
        return new ClassEntity(this.name, this.description, this.schedule);
    }

     // 3. Actualizar Entidad existente con datos del DTO (Se usa en el PUT)
    public void updateEntity(ClassEntity entity) {
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setSchedule(this.schedule);
    }
}