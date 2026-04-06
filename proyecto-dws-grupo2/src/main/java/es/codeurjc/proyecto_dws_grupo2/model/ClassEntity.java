package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.*;
import java.util.HashSet; // Cambiado: Importamos Set y HashSet
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String imageUrl;
    private String schedule;

    // CAMBIO IMPORTANTE: Usamos Set tanto en la declaración como en la inicialización
    @ManyToMany(mappedBy = "enrolledClasses")
    private Set<User> attendees = new HashSet<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // Constructor protegido para JPA
    protected ClassEntity() {}

    public ClassEntity(String name, String description, String schedule) {
        this.name = name;
        this.description = description;
        this.schedule = schedule;
    }

    // MÉTODOS DE IDENTIDAD (Claves para que .remove() borre en la base de datos)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassEntity)) return false;
        ClassEntity that = (ClassEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        // Usamos un número fijo para que el objeto sea "encontrable" 
        // siempre por Hibernate dentro de una colección Set
        return 31; 
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    // Cambiado a Set
    public Set<User> getAttendees() { return attendees; }
    public void setAttendees(Set<User> attendees) { this.attendees = attendees; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}