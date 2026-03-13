package es.codeurjc.proyecto_dws_grupo2.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;

    @ManyToMany(mappedBy = "clasesApuntadas")
    private List<User> asistentes = new ArrayList<>();

    protected Clase() {}

    public Clase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<User> getAsistentes() { return asistentes; }
    public void setAsistentes(List<User> asistentes) { this.asistentes = asistentes; }
}