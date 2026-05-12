package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService;
import es.codeurjc.proyecto_dws_grupo2.service.ReviewService;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@Controller
public class IndexController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final ClassService classService;
    private final ReviewService reviewService;

    public IndexController(UserService userService, ServiceService serviceService, ClassService classService,
            ReviewService reviewService) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.classService = classService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        addSessionUser(model, principal);
        model.addAttribute("services", serviceService.findAll());
        model.addAttribute("classes", classService.findAll());
        model.addAttribute("reviews", reviewService.getReviews());
        model.addAttribute("activeInicio", true);
        return "index";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model, Principal principal) {
        addSessionUser(model, principal);
        model.addAttribute("activeAbout", true);
        return "about";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model, Principal principal) {
        addSessionUser(model, principal);
        model.addAttribute("activeContact", true);
        return "contact";
    }

    @GetMapping("/feature")
    public String showFeaturePage(Model model, Principal principal) {
        addSessionUser(model, principal);
        model.addAttribute("services", serviceService.findAll());
        model.addAttribute("activeFeature", true);
        return "feature";
    }

    private void addSessionUser(Model model, Principal principal) {
        if (principal == null) {
            return;
        }

        User user = userService.findByEmail(principal.getName());
        if (user != null) {
            model.addAttribute("usuarioSesion", user);
        }
    }
}
