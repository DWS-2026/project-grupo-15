package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ReviewService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/reviews")
    public String showReviews(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Review> reviews = reviewService.getReviewsByUser(user);

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

        reviewService.createReviewForUser(about, rating, comment, principal.getName());
        return "redirect:/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        reviewService.deleteReviewIfAllowed(id, principal.getName());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
