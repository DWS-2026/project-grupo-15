package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;

import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity; 


    
@Controller
public class ClassController {

    private final ClassRepository classRepository;

    public ClassController(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @GetMapping("/classes/{id}")
    public String verDetalleClase(@PathVariable Long id, Model model) {
      
        Optional<ClassEntity> classOptional = classRepository.findById(id);

        if (classOptional.isPresent()) {
            model.addAttribute("clase", classOptional.get());
            return "class-detail";
        } else {
            return "redirect:/classes"; 
        }
    }


    @GetMapping("/classes")
    public String mostrarServicios(Model model) {
        model.addAttribute("clases", classRepository.findAll());
        return "clases-listado"; 
    }
}
