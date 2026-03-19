package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;

import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity; // Asegúrate de importar tu entidad


    
@Controller
public class ClassController {

    private final ClassRepository classRepository;

    public ClassController(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @GetMapping("/classes/{id}")
    public String verDetalleClase(@PathVariable Long id, Model model) {
        
        // Buscamos la clase en la base de datos por su ID
        Optional<ClassEntity> classOptional = classRepository.findById(id);

        if (classOptional.isPresent()) {
            // Si la encuentra, la metemos en el modelo con el nombre "clase"
            model.addAttribute("clase", classOptional.get());
            
            // Cargamos la NUEVA página que vamos a crear
            return "class-detail"; 
        } else {
            // Si no existe, devolvemos al usuario a la lista general
            return "redirect:/classes"; 
        }
    }


    @GetMapping("/classes")
    public String mostrarServicios(Model model) {
        
        // Le pedimos a la Base de Datos todas las clases que haya guardadas
        model.addAttribute("clases", classRepository.findAll());
        
        // Devolvemos la plantilla HTML que ya tenéis creada
        return "clases-listado"; 
    }
}
