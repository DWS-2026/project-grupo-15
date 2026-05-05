package es.codeurjc.proyecto_dws_grupo2.dto;

public record ServiceDTO(
        Long id,
        String name,
        String description,
        double price,
        String imageUrl,
        String serviceKey) {
}