package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.codeurjc.proyecto_dws_grupo2.repository.ClaseRepository;

@Controller
public class WebController {

    // Inyectamos el repositorio para poder acceder a la base de datos
    @Autowired
    private ClaseRepository claseRepository;

    @GetMapping("/")
    public String index() {
        return "index"; 
    }
    
    @GetMapping("/admin-clases")
    public String adminClases(Model model) {
        // Pedimos TODAS las clases a la base de datos y las enviamos al HTML
        model.addAttribute("clases", claseRepository.findAll());

        return "admin-clases";
    }
}