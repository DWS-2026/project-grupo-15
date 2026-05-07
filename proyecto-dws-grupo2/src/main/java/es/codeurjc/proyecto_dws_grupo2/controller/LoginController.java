package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginError(
            @RequestParam(required = false) Boolean locked,
            @RequestParam(required = false) Integer remaining,
            Model model) {

        if (Boolean.TRUE.equals(locked)) {
            // Account is fully blocked — show a specific locked message
            model.addAttribute("errorLocked", true);
        } else {
            // Normal bad credentials — show remaining attempts
            model.addAttribute("error", "Email o contraseña incorrectos");
            if (remaining != null) {
                if (remaining == 0) {
                    // This was the last attempt before locking
                    model.addAttribute("remainingZero", true);
                } else {
                    model.addAttribute("remaining", remaining);
                }
            }
        }

        return "login";
    }
}