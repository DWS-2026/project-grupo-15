package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Controller
public class RegisterController {

    private final UserRepository userRepository;

    public RegisterController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "registro";
    }

     @PostMapping("/register")
    public String pagar(User user, Model model) {

        double total = 29.99;

        if (user.isExtraPhysio()) total += 39.99;
        if (user.isExtraNutrition()) total += 29.99;
        if (user.isExtraDrinks()) total += 2.99;

        model.addAttribute("user", user);
        model.addAttribute("total", total);
        model.addAttribute("origin", "register");
        model.addAttribute("fromRegister", true);

        return "payment";
    }
}
