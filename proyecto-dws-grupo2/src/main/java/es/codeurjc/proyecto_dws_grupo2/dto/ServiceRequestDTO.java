package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;

public record ServiceRequestDTO(
    String name,
    String description,
    double price,
    String serviceKey
) {
    // 1. From DTO to new entity
    public ServiceEntity toEntity() {
        ServiceEntity service = new ServiceEntity();
        service.setName(this.name);
        service.setDescription(this.description);
        service.setPrice(this.price);
        service.setServiceKey(this.serviceKey);
        return service;
    }

    // 2. Update entity with data from DTO
    public void updateEntity(ServiceEntity entity) {
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setPrice(this.price);
        entity.setServiceKey(this.serviceKey);
    }
}