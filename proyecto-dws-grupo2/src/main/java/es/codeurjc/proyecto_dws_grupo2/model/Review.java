package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String about;
    private int rating;
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne
    private ClassEntity classEntity;

    public Review() {
    }

    public Review(int rating, String comment, User user) {
        this.rating = rating;
        this.comment = comment;
        this.user = user;
    }

    public boolean isRating5() {
        return this.rating == 5;
    }

    public boolean isRating4() {
        return this.rating == 4;
    }

    public boolean isRating3() {
        return this.rating == 3;
    }

    public boolean isRating2() {
        return this.rating == 2;
    }

    public boolean isRating1() {
        return this.rating == 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }
}