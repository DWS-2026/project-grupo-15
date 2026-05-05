package es.codeurjc.proyecto_dws_grupo2.controller.api;

import es.codeurjc.proyecto_dws_grupo2.dto.ReviewRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ReviewResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.UserResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // Conversión de entidad a ResponseDTO (con usuario completo)
    private ReviewResponseDTO toResponseDTO(Review review) {
        if (review == null) return null;
        return new ReviewResponseDTO(
            review.getId(),
            review.getAbout(),
            review.getRating(),
            review.getComment(),
            review.getUser() != null ? new UserResponseDTO(review.getUser()) : null
        );
    }

    @GetMapping("/")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviews(Pageable pageable) {
        Page<ReviewResponseDTO> responsePage = reviewService.getReviews(pageable)
                .map(this::toResponseDTO);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long id) {
        Optional<Review> reviewOptional = reviewService.getReview(id);
        if (reviewOptional.isPresent()) {
            return ResponseEntity.ok(toResponseDTO(reviewOptional.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO requestDTO) {
        // Crear entidad a partir del RequestDTO
        Review review = new Review();
        review.setAbout(requestDTO.about());
        review.setRating(requestDTO.rating());
        review.setComment(requestDTO.comment());

        Review created = reviewService.createReview(review, requestDTO.userId(), requestDTO.classEntityId());
        ReviewResponseDTO responseDTO = toResponseDTO(created);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> replaceReview(@PathVariable Long id, @RequestBody ReviewRequestDTO requestDTO) {
        Review review = new Review();
        review.setAbout(requestDTO.about());
        review.setRating(requestDTO.rating());
        review.setComment(requestDTO.comment());

        Review updated = reviewService.replaceReview(id, review, requestDTO.userId(), requestDTO.classEntityId());
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long id) {
        Optional<Review> deletedOptional = reviewService.deleteReview(id);
        if (deletedOptional.isPresent()) {
            return ResponseEntity.ok(toResponseDTO(deletedOptional.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}