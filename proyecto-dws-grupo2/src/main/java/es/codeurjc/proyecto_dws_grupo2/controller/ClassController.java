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
import es.codeurjc.proyecto_dws_grupo2.service.ClassService; 
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@Controller
public class ClassController {

    private final ClassService classService;
    private final UserService userService;

    public ClassController(ClassService classService, UserService userService) {
        this.classService = classService;
        this.userService = userService;
    }


    private void loadUserIntoMenu(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user != null) {
                model.addAttribute("loggedUser", user); 
            }
        }
    }

    @GetMapping("/classes/info") 
    public String showClassInfo(Model model, Principal principal) {
        loadUserIntoMenu(model, principal); 
        model.addAttribute("classes", classService.findAll()); 
        return "class"; 
    }

    @GetMapping("/classes")
    public String showClasses(Model model, Principal principal) {
        loadUserIntoMenu(model, principal); 
        model.addAttribute("classes", classService.findAll());
        return "class-list"; 
    }

    @GetMapping("/classes/{id}")
    public String showClassDetail(@PathVariable Long id, Model model, Principal principal) {
        loadUserIntoMenu(model, principal); 
        
        ClassEntity classEntity = classService.findById(id).orElse(null);
        if (classEntity == null) return "redirect:/classes";

        model.addAttribute("class", classEntity); 

        if (principal != null) {
            boolean isEnrolled = classService.isUserEnrolled(id, principal.getName());
            model.addAttribute("isEnrolled", isEnrolled);
        }

        return "class-detail"; 
    }

    @PostMapping("/classes/{id}/signup")
    public String signUpForClass(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
        if (principal != null) {
            classService.enrollUser(id, principal.getName());
             attributes.addFlashAttribute("mensajeExito", "¡Genial! Te has apuntado correctamente.");
        }
        return "redirect:/classes";
    }

    @PostMapping("/classes/{id}/leave")
    public String leaveClass(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            classService.unenrollUser(id, principal.getName());
        }
        return "redirect:/classes/" + id;
    }
}
