package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet; 
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;

    private String schedule;

    @ManyToMany(mappedBy = "enrolledClasses")
    private Set<User> attendees = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    protected ClassEntity() {
    }

    public ClassEntity(String name, String description, String schedule) {
        this.name = name;
        this.description = description;
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ClassEntity))
            return false;
        ClassEntity that = (ClassEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // CAMBIO: Getter y Setter adaptados a la nueva entidad Image
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Set<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<User> attendees) {
        this.attendees = attendees;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}