package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;

import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity; 


    
@Controller
public class ServiceController {

    private final ClassRepository classRepository;

    public ServiceController(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

        @GetMapping("/services")
    public String showRegisterForm() {
        return "services";
    }


}
