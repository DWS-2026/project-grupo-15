package es.codeurjc.proyecto_dws_grupo2.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <-- ESTA LÍNEA ARREGLA EL ERROR
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClaseRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Service
public class DataBaseInitializer {

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private UserRepository userRepository;

   @PostConstruct
    public void init() {
        
        // 1. Creamos y guardamos las Clases
        Clase crossfit = new Clase("CrossFit", "Entrenamiento funcional intenso");
        Clase zumba = new Clase("Zumba", "Baile y cardio divertido");
        Clase bodyPump = new Clase("Body Pump", "Pesas y música");

        claseRepository.save(crossfit);
        claseRepository.save(zumba);
        claseRepository.save(bodyPump);

        // 2. Creamos Usuarios usando tu molde (con apellidos, extras, etc.)
        User user1 = new User();
        user1.setNombre("Paco");
        user1.setApellidos("García");
        user1.setEmail("paco@gmail.com");
        user1.setPassword("1234");
        user1.setExtraFisio(true);

        User admin = new User();
        admin.setName("Laura");
        admin.setApellidos("Admin");
        admin.setEmail("admin@titangym.com");
        admin.setPassword("admin123");

        // 3. ¡LA MAGIA! Apuntamos a Paco a CrossFit y a Zumba
        user1.getClasesApuntadas().add(crossfit);
        user1.getClasesApuntadas().add(zumba);

        // 4. Guardamos los usuarios en la base de datos
        userRepository.save(user1);
        userRepository.save(admin);
    }
}