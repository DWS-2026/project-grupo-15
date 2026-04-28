package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import java.security.Principal;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService; // Importamos el servicio
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository; 

@Controller
public class ClassController {

    private final ClassService classService; // Ya no usamos los repositorios directamente para las clases
    private final UserRepository userRepository; // Lo mantenemos solo para el menú de navegación

    public ClassController(ClassService classService, UserRepository userRepository) {
        this.classService = classService;
        this.userRepository = userRepository;
    }

    private void cargarUsuarioEnMenu(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("usuarioSesion", user);
            }
        }
    }

    @GetMapping("/classes/info") 
    public String mostrarInfoClases(Model model, Principal principal) {
        cargarUsuarioEnMenu(model, principal); 
        model.addAttribute("clases", classService.findAll()); // Llamamos al servicio
        return "class"; 
    }

    @GetMapping("/classes")
    public String mostrarClases(Model model, Principal principal) {
        cargarUsuarioEnMenu(model, principal); 
        model.addAttribute("clases", classService.findAll()); // Llamamos al servicio
        return "class-list"; 
    }

    @GetMapping("/classes/{id}")
    public String verDetalleClase(@PathVariable Long id, Model model, Principal principal) {
        cargarUsuarioEnMenu(model, principal); 
        
        ClassEntity clase = classService.findById(id).orElse(null); // Llamamos al servicio
        if (clase == null) return "redirect:/classes";

        model.addAttribute("clase", clase);

        if (principal != null) {
            boolean isEnrolled = classService.isUserEnrolled(id, principal.getName());
            model.addAttribute("isEnrolled", isEnrolled);
        }

        return "class-detail"; 
    }

    @PostMapping("/classes/{id}/signup")
    public String apuntarseAClase(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
        if (principal != null) {
            classService.enrollUser(id, principal.getName()); // ¡Toda la lógica reducida a una línea!
            attributes.addFlashAttribute("mensajeExito", "¡Genial! Te has apuntado correctamente.");
        }
        return "redirect:/classes";
    }

    @PostMapping("/classes/{id}/leave")
    public String desapuntarseDeClase(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            classService.unenrollUser(id, principal.getName()); // Toda la lógica delegada
        }
        return "redirect:/classes/" + id;
    }
}