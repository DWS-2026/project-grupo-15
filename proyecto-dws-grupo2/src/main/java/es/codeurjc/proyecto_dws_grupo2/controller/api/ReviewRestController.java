package es.codeurjc.proyecto_dws_grupo2.controller.api;

import es.codeurjc.proyecto_dws_grupo2.dto.ReviewDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ReviewMapper;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/")
    public ResponseEntity<Page<ReviewDTO>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviewDTOs = reviewService.getReviews(pageable)
                .map(reviewMapper::toDTO);
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id) {
        Optional<Review> reviewOptional = reviewService.getReview(id);
        if (reviewOptional.isPresent()) {
            return ResponseEntity.ok(reviewMapper.toDTO(reviewOptional.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        Review review = reviewMapper.toDomain(reviewDTO);
        review = reviewService.createReview(review, reviewDTO.userId(), reviewDTO.classEntityId());
        ReviewDTO responseDTO = reviewMapper.toDTO(review);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> replaceReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        Review updatedReview = reviewMapper.toDomain(reviewDTO);
        updatedReview = reviewService.replaceReview(id, updatedReview, reviewDTO.userId(), reviewDTO.classEntityId());
        return ResponseEntity.ok(reviewMapper.toDTO(updatedReview));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable Long id) {
        Optional<Review> reviewOptional = reviewService.deleteReview(id);
        if (reviewOptional.isPresent()) {
            return ResponseEntity.ok(reviewMapper.toDTO(reviewOptional.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}