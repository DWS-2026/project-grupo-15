package es.codeurjc.proyecto_dws_grupo2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/reviews")
    public String showReviews(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null) return "redirect:/login";

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
                             HttpSession session) {

        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null) return "redirect:/login";

        Review review = new Review();
        review.setAbout(about);
        review.setRating(rating);
        review.setComment(comment);
        review.setUser(user);

        reviewRepository.save(review);

        return "redirect:/reviews";
    }
}