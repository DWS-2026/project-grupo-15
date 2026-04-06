package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal; // El nuevo portero
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;

@Controller
public class IndexController {

    private final UserRepository userRepository;
    
    private final ServiceRepository serviceRepository; 

    private final ClassRepository classRepository; 

    private final ReviewRepository reviewRepository;

    public IndexController(UserRepository userRepository, ServiceRepository serviceRepository, ClassRepository classRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.classRepository = classRepository;
        this.reviewRepository = reviewRepository;
    }

   @GetMapping("/")
    public String index(Model model, Principal principal) {
        
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            
            if (user != null) {
            
                model.addAttribute("usuarioSesion", user);
            }
        }

        model.addAttribute("services", serviceRepository.findAll());
        model.addAttribute("classes", classRepository.findAll());
        model.addAttribute("reviews", reviewRepository.findAll());
        model.addAttribute("activeInicio", true);
        return "index";
    }


    @GetMapping("/about")
    public String showAboutPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("usuarioSesion", user);
            }
        }
        model.addAttribute("activeAbout", true);
        return "about";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("usuarioSesion", user);
            }
        }

        model.addAttribute("activeContact", true);
        return "contact";
    }

    @GetMapping("/feature")
    public String showFeaturePage(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("usuarioSesion", user);
            }
        }
         model.addAttribute("services", serviceRepository.findAll());
         model.addAttribute("activeFeature", true);
        return "feature";
    }
}