package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceMapper {

    // 1. Método para la API REST (por defecto enrolled = false)
    public ServiceDTO toDTO(ServiceEntity service) {
        return new ServiceDTO(
            service.getId(),
            service.getName(),
            service.getDescription(),
            service.getPrice(),
            service.getImageUrl(), 
            service.getServiceKey(),
            false 
        );
    }

    // 2. Método para la Web (HTML), donde sí sabemos si está suscrito
    public ServiceDTO toDTOWithEnrolled(ServiceEntity service, boolean isEnrolled) {
        return new ServiceDTO(
            service.getId(),
            service.getName(),
            service.getDescription(),
            service.getPrice(),
            service.getImageUrl(),
            service.getServiceKey(),
            isEnrolled
        );
    }

    // 3. De Lista de Entidades a Lista de DTOs
    public List<ServiceDTO> toDTOs(Collection<ServiceEntity> services) {
        return services.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // 4. De DTO a Entidad
    public ServiceEntity toDomain(ServiceDTO dto) {
        ServiceEntity service = new ServiceEntity();
        
        service.setName(dto.name());
        service.setDescription(dto.description());
        service.setPrice(dto.price());
        service.setImageUrl(dto.imageUrl()); 
        service.setServiceKey(dto.serviceKey());
        
        return service;
    }
}