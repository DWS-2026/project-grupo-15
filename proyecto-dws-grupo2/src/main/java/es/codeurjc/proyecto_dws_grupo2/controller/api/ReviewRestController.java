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
import es.codeurjc.proyecto_dws_grupo2.model.User;
import java.net.URI;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

// --- Imports de Swagger / OpenAPI ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reseñas", description = "Gestión de valoraciones y comentarios de los usuarios")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // CORRECCIÓN 1: Vuelve a tener 5 parámetros exactos como en tu código original
    private ReviewResponseDTO toResponseDTO(Review review) {
        if (review == null)
            return null;
        return new ReviewResponseDTO(
                review.getId(),
                review.getAbout(),
                review.getRating(),
                review.getComment(),
                review.getUser() != null ? new UserResponseDTO(review.getUser()) : null);
    }

    @Operation(summary = "Obtener todas las reseñas", description = "Devuelve una página con todas las valoraciones de los clientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reseñas recuperada con éxito")
    })
    @GetMapping("/")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviews(Pageable pageable) {
        // CORRECCIÓN 2: Usamos getReviews(pageable)
        Page<ReviewResponseDTO> reviews = reviewService.getReviews(pageable).map(this::toResponseDTO);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Obtener reseña por ID", description = "Busca una valoración específica por su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna reseña con ese ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long id) {
        // CORRECCIÓN 3: Usamos getReview(id)
        return reviewService.getReview(id)
                .map(review -> ResponseEntity.ok(toResponseDTO(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Publicar una nueva reseña", description = "Crea un comentario y una puntuación asociada a una clase y un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña publicada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de la reseña inválidos", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @RequestBody ReviewRequestDTO requestDTO,
            @RequestAttribute("user") User currentUser) {
        Review review = new Review();
        review.setAbout(requestDTO.about());
        review.setRating(requestDTO.rating());
        review.setComment(requestDTO.comment());

        Review created = reviewService.createReview(review, currentUser.getId(), requestDTO.classEntityId());
        ReviewResponseDTO responseDTO = toResponseDTO(created);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Actualizar una reseña", description = "Modifica el contenido o la nota de una reseña existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> replaceReview(@PathVariable Long id,
            @RequestBody ReviewRequestDTO requestDTO,
            @RequestAttribute("user") User currentUser) {

        Optional<Review> existingOpt = reviewService.getReview(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existing = existingOpt.get();
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        if (existing.getUser() != null && !existing.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }
        Review review = new Review();
        review.setAbout(requestDTO.about());
        review.setRating(requestDTO.rating());
        review.setComment(requestDTO.comment());

        Review updated = reviewService.replaceReview(id, review, currentUser.getId(), requestDTO.classEntityId());
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    @Operation(summary = "Eliminar una reseña", description = "Borra permanentemente un comentario. Requiere permisos de administrador o del autor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long id, @RequestAttribute("user") User currentUser) {

        Optional<Review> existingOpt = reviewService.getReview(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existing = existingOpt.get();
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        if (existing.getUser() != null && !existing.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        Optional<Review> deletedOptional = reviewService.deleteReview(id);
        return ResponseEntity.ok(toResponseDTO(deletedOptional.get()));
    }
}