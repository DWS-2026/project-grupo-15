package es.codeurjc.proyecto_dws_grupo2.dto;

public record ReviewRequestDTO(
    String about,
    int rating,
    String comment,
    Long userId,
    Long classEntityId
) {}