package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.Review;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
            review.getId(),
            review.getAbout(),
            review.getRating(),
            review.getComment(),
            review.getUser() != null ? review.getUser().getId() : null,
            review.getClassEntity() != null ? review.getClassEntity().getId() : null
        );
    }

    public List<ReviewDTO> toDTOs(Collection<Review> reviews) {
        return reviews.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public Review toDomain(ReviewDTO dto) {
        Review review = new Review();
        review.setAbout(dto.about());
        review.setRating(dto.rating());
        review.setComment(dto.comment());
        // User y ClassEntity se resuelven en el Service por ID
        return review;
    }
}