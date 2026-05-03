package es.codeurjc.proyecto_dws_grupo2.dto;

public record ReviewDTO(
    Long id,
    String about,
    int rating,
    String comment,
    Long userId,
    Long classEntityId
) {}