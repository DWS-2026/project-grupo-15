package es.codeurjc.proyecto_dws_grupo2.controller; // Asegúrate de que el paquete coincida con tus carpetas

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.codeurjc.proyecto_dws_grupo2.model.Clase;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        // Esto busca el archivo "index.html" dentro de la carpeta templates
        return "index"; 
    }
    
    @GetMapping("/admin-clases")
    public String adminClases(Model model) {

        //1. We create a "fake" list (simulating a data base)
        List<Clase> listaDeClases = new ArrayList<>();
        listaDeClases.add(new Clase(1, "CrossFit", "Entrenamiento funcional intenso"));
        listaDeClases.add(new Clase(2, "Zumba", "Baile y cardio divertido"));
        listaDeClases.add(new Clase(3, "Body Pump", "Pesas y música"));

        //2. We introduce the list in the Model to send it to the HTML
        model.addAttribute("clases", listaDeClases);

        return "admin-clases";
    }

    // Tendrás que ir añadiendo aquí el resto de páginas poco a poco
}