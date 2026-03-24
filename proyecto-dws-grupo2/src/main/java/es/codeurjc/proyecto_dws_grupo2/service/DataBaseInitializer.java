package es.codeurjc.proyecto_dws_grupo2.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Service
public class DataBaseInitializer {

    @Autowired
    private ClassRepository claseRepository;

    @Autowired
    private UserRepository userRepository;

   @PostConstruct
    public void init() {
        
        ClassEntity crossfit = new ClassEntity("CrossFit", "Entrenamiento funcional intenso", "Lunes 18:00-19:00");
        ClassEntity zumba = new ClassEntity("Zumba", "Baile y cardio divertido", "Miércoles 19:00-20:00");
        ClassEntity bodyPump = new ClassEntity("Body Pump", "Pesas y música", "Viernes 17:00-18:00");

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