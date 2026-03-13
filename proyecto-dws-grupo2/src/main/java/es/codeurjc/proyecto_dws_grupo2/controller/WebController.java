package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    // Página principal
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("users", userService.getAllUsers()); // opcional
        return "index";
    }

    // Panel admin
    @GetMapping("/admin-clases")
    public String adminClases() {
        return "admin-clases";
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "registro";
    }

    // Guardar usuario en BD
    @PostMapping("/registro")
    public String registerUser(User user) {
        userService.saveUser(user);
        return "redirect:/";
    }

    // Borrar usuario (opcional)
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/";
    }
}