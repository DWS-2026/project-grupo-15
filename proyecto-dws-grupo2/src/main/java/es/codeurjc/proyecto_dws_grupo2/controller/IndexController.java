package es.codeurjc.proyecto_dws_grupo2.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.codeurjc.proyecto_dws_grupo2.model.User;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogado");

        if (user != null) {
            model.addAttribute("logeado", true);
            model.addAttribute("userName", user.getFirstName());
        } else {
            model.addAttribute("logeado", false);
        }

        return "index"; // Esto buscará src/main/resources/templates/index.html (o .mustache)
    }
}