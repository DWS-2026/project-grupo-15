package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE) // Borra roles automáticamente
    private List<String> roles = new ArrayList<>();

    private boolean extraPhysio;
    private boolean extraNutrition;
    private boolean extraDrinks;

    private String profileImageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_classes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE) // Borra la unión en la tabla intermedia
    private List<ClassEntity> enrolledClasses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_activities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Activity> enrolledActivities = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_services",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ServiceEntity> enrolledServices = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public User() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isExtraPhysio() { return extraPhysio; }
    public void setExtraPhysio(boolean extraPhysio) { this.extraPhysio = extraPhysio; }

    public boolean isExtraNutrition() { return extraNutrition; }
    public void setExtraNutrition(boolean extraNutrition) { this.extraNutrition = extraNutrition; }

    public boolean isExtraDrinks() { return extraDrinks; }
    public void setExtraDrinks(boolean extraDrinks) { this.extraDrinks = extraDrinks; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public List<ClassEntity> getEnrolledClasses() { return enrolledClasses; }
    public void setEnrolledClasses(List<ClassEntity> enrolledClasses) { this.enrolledClasses = enrolledClasses; }

    public List<Activity> getEnrolledActivities() { return enrolledActivities; }
    public void setEnrolledActivities(List<Activity> enrolledActivities) { this.enrolledActivities = enrolledActivities; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public List<ServiceEntity> getEnrolledServices() { return enrolledServices; }
    public void setEnrolledServices(List<ServiceEntity> enrolledServices) { this.enrolledServices = enrolledServices; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}