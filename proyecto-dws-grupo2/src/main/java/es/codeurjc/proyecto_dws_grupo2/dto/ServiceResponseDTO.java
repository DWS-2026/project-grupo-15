package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;

public record ServiceResponseDTO(
    Long id,
    String name,
    String description,
    double price,
    String serviceKey,
    boolean enrolled,
    ImageResponseDTO image
) {
    // Constructor 1: Para la API (Por defecto enrolled = false)
    public ServiceResponseDTO(ServiceEntity service) {
        this(service, false); 
    }

    // Constructor 2: Para la WEB (Le pasamos si está suscrito o no)
    public ServiceResponseDTO(ServiceEntity service, boolean enrolled) {
        this(
            service.getId(),
            service.getName(),
            service.getDescription(),
            service.getPrice(),
            service.getServiceKey(),
            enrolled, 
            service.getImage() != null ? new ImageResponseDTO(service.getImage()) : null
        );
    }
}