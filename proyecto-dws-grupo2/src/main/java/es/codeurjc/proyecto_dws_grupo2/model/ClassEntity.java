package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String imageUrl;

    @ManyToMany(mappedBy = "enrolledClasses")
    private List<User> attendees = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    protected ClassEntity() {
    }

    public ClassEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and setters

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<User> attendees) {
        this.attendees = attendees;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
