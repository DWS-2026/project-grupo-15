package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {

    private final UserRepository userRepository;

    public PaymentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/pago-exitoso")
    public String pagoExitoso(HttpSession session, Model model) {

        User usuarioPendiente = (User) session.getAttribute("usuarioPendiente");

        if (usuarioPendiente == null) {
            return "registro";
        }

        userRepository.save(usuarioPendiente);

        session.removeAttribute("usuarioPendiente");
        session.setAttribute("usuarioLogado", usuarioPendiente);

        model.addAttribute("user", usuarioPendiente);
        
        return "successful";
    }
}