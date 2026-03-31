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

        ClassEntity crossfit = new ClassEntity(
                "CrossFit",
                "En esta clase de CrossFit no solo vienes a levantar pesas; vienes a compartir el esfuerzo con un equipo que te apoya en cada repetición. Aquí celebramos desde el primer pull-up hasta el último segundo del cronómetro. Si buscas un ambiente divertido, dinámico y que te mantenga motivado, ¡esta es tu clase!",
                "Lunes 18:00-19:00");
        crossfit.setImageUrl("/img/feature-1.jpg");

        ClassEntity bodyPump = new ClassEntity(
                "Body Pump",
                "Entrenamiento con pesas al ritmo de la música que fortalece y tonifica todo el cuerpo. Mejora tu resistencia, quema calorías y gana fuerza en una sola clase. ¡Apta para todos los niveles!",
                "Viernes 17:00-18:00");
        bodyPump.setImageUrl("/img/feature-2.jpg");

        ClassEntity spinning = new ClassEntity(
                "Spinning",
                "Pedalea al ritmo de la música y quema calorías al máximo. Entrenamiento intenso que mejora tu resistencia, fortalece piernas y te llena de energía. ¡Siente la adrenalina en cada clase!",
                "Martes 10:00-11:00");
        spinning.setImageUrl("/img/spinning.jpg");

        ClassEntity zumba = new ClassEntity(
                "Zumba",
                "Baila, quema calorías y diviértete sin darte cuenta. Coreografías fáciles, música latina y buen rollo para ponerte en forma disfrutando. ¡Sonríe, muévete y repite!",
                "Miércoles 19:00-20:00");
        zumba.setImageUrl("/img/zumba.jpg");

        claseRepository.save(crossfit);
        claseRepository.save(bodyPump);
        claseRepository.save(spinning);
        claseRepository.save(zumba);

        if(userRepository.findByEmail("paco@gmail.com").isEmpty()) {
        User user1 = new User();
        user1.setFirstName("Paco");
        user1.setLastName("García");
        user1.setEmail("paco@gmail.com");
        user1.setPassword("1234");
        user1.setExtraPhysio(true);
        user1.getEnrolledClasses().add(crossfit);
        user1.getEnrolledClasses().add(zumba);
        userRepository.save(user1);
        }

        if(userRepository.findByEmail("admin@titangym.com").isEmpty()) {
        User admin = new User();
        admin.setFirstName("Laura");
        admin.setLastName("Admin");
        admin.setEmail("admin@titangym.com");
        admin.setPassword("admin123");
        userRepository.save(admin);
        }

        
        
    }
}