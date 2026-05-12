package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal; // <-- Importamos Principal
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository; // <-- Importamos el UserRepository
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository; // Lo añadimos para poder buscar al usuario

    // Actualizamos el constructor para que reciba ambos repositorios
    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/reviews")
    public String showReviews(Model model, Principal principal) {
        
        // Sacamos al usuario directamente de la base de datos gracias a Principal
        User user = userRepository.findByEmail(principal.getName()).orElse(null);

        List<Review> reviews = reviewRepository.findByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("activeReviews", true);
        
        return "reviews";
    }

    @PostMapping("/reviews")
    public String addReview(@RequestParam String about,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal) { 

        
        User user = userRepository.findByEmail(principal.getName()).orElse(null);

        Review review = new Review();
        review.setAbout(about);
        review.setRating(rating);
        review.setComment(comment);
        review.setUser(user);

        reviewRepository.save(review);

        return "redirect:/reviews";

        
    }
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        
        User currentUser = userRepository.findByEmail(principal.getName()).orElse(null);
        Review review = reviewRepository.findById(id).orElse(null);

        // Seguridad: borra si es el dueño o si es un ADMIN
        if (review != null && currentUser != null) {
            if (review.getUser().getId().equals(currentUser.getId()) || currentUser.getRoles().contains("ADMIN")) {
                reviewRepository.deleteById(id);
            }
        }

        
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}