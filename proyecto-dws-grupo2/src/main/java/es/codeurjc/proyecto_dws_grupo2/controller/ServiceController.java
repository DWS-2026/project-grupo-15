package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;


@Controller
public class ServiceController {

    @GetMapping("/services")
    public String services(HttpSession session, Model model) {

        User user = (User) session.getAttribute("usuarioLogado");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "servicios-listado";
    }
}