package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;

public class ClassDTO {
    
    private Long id;
    private String name;
    private String description;
    private String schedule;
    private String imageUrl;

    // Constructor que convierte la Entidad en DTO automáticamente
    public ClassDTO(ClassEntity clase) {
        this.id = clase.getId();
        this.name = clase.getName();
        this.description = clase.getDescription();
        this.schedule = clase.getSchedule();
        this.imageUrl = clase.getImageUrl();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}