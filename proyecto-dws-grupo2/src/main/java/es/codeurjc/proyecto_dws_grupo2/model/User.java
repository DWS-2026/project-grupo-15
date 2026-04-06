package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profileImageUrl = "/img/avatar.jpg";


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<String> roles = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_classes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "class_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ClassEntity> enrolledClasses = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_services", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "service_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "user_id", "service_id" }))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ServiceEntity> enrolledServices = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public User() {
    }

    // --- GETTERS Y SETTERS CORREGIDOS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Corregido: Ahora devuelve y recibe Set
    public Set<ClassEntity> getEnrolledClasses() {
        return enrolledClasses;
    }

    public void setEnrolledClasses(Set<ClassEntity> enrolledClasses) {
        this.enrolledClasses = enrolledClasses;
    }

    // Corregido: Ahora devuelve y recibe Set
    public Set<ServiceEntity> getEnrolledServices() {
        return enrolledServices;
    }

    public void setEnrolledServices(Set<ServiceEntity> enrolledServices) {
        this.enrolledServices = enrolledServices;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}