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
    // Predeterminated: enrolled false
    public ServiceResponseDTO(ServiceEntity service) {
        this(service, false); 
    }

    // Send to web if the user is suscribed or not
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