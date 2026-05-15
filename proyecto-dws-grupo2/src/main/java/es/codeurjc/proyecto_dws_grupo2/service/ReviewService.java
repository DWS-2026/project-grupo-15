package es.codeurjc.proyecto_dws_grupo2.service;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
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

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public Page<Review> getReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
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

    public Review createReview(Review review, String username, Long classEntityId) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        review.setUser(user);

        if (classEntityId != null) {
            ClassEntity classEntity = classRepository.findById(classEntityId)
                    .orElseThrow(() -> new RuntimeException("ClassEntity not found with id: " + classEntityId));
            review.setClassEntity(classEntity);
        }

        return reviewRepository.save(review);
    }

    public Review replaceReview(Long id, Review updatedReview, Long classEntityId) {

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        existingReview.setAbout(updatedReview.getAbout());
        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());

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

    public boolean isOwnerOrAdmin(Review review, String email) {
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        boolean isOwner = review.getUser() != null && review.getUser().getId().equals(currentUser.getId());
        return isOwner || isAdmin;
    }

    public void deleteReviewIfAllowed(Long id, String email) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent() && isOwnerOrAdmin(review.get(), email)) {
            reviewRepository.deleteById(id);
        }
    }

    public void createReviewForUser(String about, int rating, String comment, String username) {
        Review review = new Review();
        review.setAbout(about);
        review.setRating(rating);
        review.setComment(comment);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        review.setUser(user);

        reviewRepository.save(review);
    }

    public boolean isOwner(Review review, String email) {
        if (review == null || review.getUser() == null || review.getUser().getEmail() == null) {
            return false;
        }
        return review.getUser().getEmail().equals(email);
    }
}
