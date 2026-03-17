package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;

@Controller
public class PaymentController {

   

    @PostMapping("/pago-exitoso")
    public String pagoExitoso(User user, Model model) {
        model.addAttribute("user", user);
        return "successful";
    }
}
