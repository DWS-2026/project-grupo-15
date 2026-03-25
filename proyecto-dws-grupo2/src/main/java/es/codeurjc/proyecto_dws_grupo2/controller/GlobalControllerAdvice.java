package es.codeurjc.proyecto_dws_grupo2.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import es.codeurjc.proyecto_dws_grupo2.model.User;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("usuarioSesion")
    public User addAttributeUser(HttpSession session) {
        return (User) session.getAttribute("usuarioLogado");
    }
}