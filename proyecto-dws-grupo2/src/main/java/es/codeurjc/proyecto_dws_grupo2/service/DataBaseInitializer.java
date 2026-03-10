package es.codeurjc.proyecto_dws_grupo2.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <-- ESTA LÍNEA ARREGLA EL ERROR
import es.codeurjc.proyecto_dws_grupo2.model.Clase;
import es.codeurjc.proyecto_dws_grupo2.repository.ClaseRepository;

@Service
public class DataBaseInitializer {

    @Autowired
    private ClaseRepository claseRepository;

    @PostConstruct
    public void init() {
        claseRepository.save(new Clase("CrossFit", "Entrenamiento funcional intenso"));
        claseRepository.save(new Clase("Zumba", "Baile y cardio divertido"));
        claseRepository.save(new Clase("Body Pump", "Pesas y música"));
    }
}