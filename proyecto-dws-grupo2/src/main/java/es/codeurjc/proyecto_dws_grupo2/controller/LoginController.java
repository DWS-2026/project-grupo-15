package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class LoginController {  

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginError(Model model) {

        model.addAttribute("error", "Email o contraseña incorrectos");
        
        return "login"; 
    }
}
