package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

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
    public String pagar(User user, 
                        @RequestParam(defaultValue = "false") boolean extraPhysio,
                        @RequestParam(defaultValue = "false") boolean extraNutrition,
                        @RequestParam(defaultValue = "false") boolean extraDrinks,
                        Model model, 
                        HttpSession session) {

        double total = 29.99; // Cuota base mensual

        // 1. Calculamos el total usando los parámetros del formulario
        if (extraPhysio) total += 39.99;
        if (extraNutrition) total += 29.99;
        if (extraDrinks) total += 2.99;

        // 2. Guardamos el usuario en la sesión
        session.setAttribute("usuarioPendiente", user);
        
        // 3. Guardamos TAMBIÉN qué servicios ha elegido para poder añadirlos a la lista 
        // ManyToMany una vez que el pago se confirme en el PaymentController.
        session.setAttribute("seleccionPhysio", extraPhysio);
        session.setAttribute("seleccionNutrition", extraNutrition);
        session.setAttribute("seleccionDrinks", extraDrinks);

        model.addAttribute("user", user);
        model.addAttribute("total", total);
        model.addAttribute("origin", "register");
        model.addAttribute("fromRegister", true);
        model.addAttribute("serviceKey", "");

        return "payment";
    }
}