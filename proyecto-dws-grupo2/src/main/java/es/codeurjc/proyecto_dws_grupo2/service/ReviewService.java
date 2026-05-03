package es.codeurjc.proyecto_dws_grupo2.service;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;     
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;                             

    public Collection<Review> getReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReview(Long id) {
        return reviewRepository.findById(id);
    }

    public Review createReview(Review review, Long userId, Long classEntityId) {

        if (userId != null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            review.setUser(user);
        }

        if (classEntityId != null) {
            ClassEntity classEntity = classRepository.findById(classEntityId) 
                .orElseThrow(() -> new RuntimeException("ClassEntity not found with id: " + classEntityId));
            review.setClassEntity(classEntity);
        }

        return reviewRepository.save(review);
    }

    public Review replaceReview(Long id, Review updatedReview, Long userId, Long classEntityId) {

        Review existingReview = reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        existingReview.setAbout(updatedReview.getAbout());
        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());

        if (userId != null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            existingReview.setUser(user);
        }

        if (classEntityId != null) {
            ClassEntity classEntity = classRepository.findById(classEntityId) 
                .orElseThrow(() -> new RuntimeException("ClassEntity not found with id: " + classEntityId));
            existingReview.setClassEntity(classEntity);
        }

        return reviewRepository.save(existingReview);
    }

    public Optional<Review> deleteReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        review.ifPresent(r -> reviewRepository.deleteById(id));
        return review;
    }
}