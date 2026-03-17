package es.codeurjc.proyecto_dws_grupo2.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
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
        
        ClassEntity crossfit = new ClassEntity("CrossFit", "Entrenamiento funcional intenso");
        ClassEntity zumba = new ClassEntity("Zumba", "Baile y cardio divertido");
        ClassEntity bodyPump = new ClassEntity("Body Pump", "Pesas y música");

        claseRepository.save(crossfit);
        claseRepository.save(zumba);
        claseRepository.save(bodyPump);

        User user1 = new User();
        user1.setFirstName("Paco");
        user1.setLastName("García");
        user1.setEmail("paco@gmail.com");
        user1.setPassword("1234");
        user1.setExtraPhysio(true);

        User admin = new User();
        admin.setFirstName("Laura");
        admin.setLastName("Admin");
        admin.setEmail("admin@titangym.com");
        admin.setPassword("admin123");

        user1.getEnrolledClasses().add(crossfit);
        user1.getEnrolledClasses().add(zumba);

        userRepository.save(user1);
        userRepository.save(admin);
    }
}