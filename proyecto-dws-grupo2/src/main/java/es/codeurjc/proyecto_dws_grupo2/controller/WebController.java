package es.codeurjc.proyecto_dws_grupo2.controller; // Asegúrate de que el paquete coincida con tus carpetas

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        // Esto busca el archivo "index.html" dentro de la carpeta templates
        return "index"; 
    }
    
    @GetMapping("/admin-clases")
    public String adminClases() {
        return "admin-clases";
    }

    // Tendrás que ir añadiendo aquí el resto de páginas poco a poco
}