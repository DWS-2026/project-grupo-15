package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal; // El nuevo portero
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Controller
public class IndexController {

    // Necesitamos el repositorio para buscar su nombre de pila
    private final UserRepository userRepository;

    public IndexController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   @GetMapping("/")
    public String index(Model model, Principal principal) {
        
        if (principal != null) {
            // Buscamos al usuario en la BBDD
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            
            if (user != null) {
            
                model.addAttribute("usuarioSesion", user);
            }
        }

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
        return "feature";
    }
}