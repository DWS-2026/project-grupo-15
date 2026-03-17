package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }

        session.setAttribute("usuarioLogado", user);
        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }
}
