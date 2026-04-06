package es.codeurjc.proyecto_dws_grupo2.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Service;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class DataBaseInitializer {

    private final ClassRepository claseRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataBaseInitializer(ClassRepository claseRepository,
                               UserRepository userRepository,
                               ServiceRepository serviceRepository,
                               PasswordEncoder passwordEncoder) {
        this.claseRepository = claseRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        // --- 1. INICIALIZAR CLASES ---
        if (claseRepository.count() == 0) {
            ClassEntity crossfit = new ClassEntity(
                    "CrossFit",
                    "En esta clase de CrossFit no solo vienes a levantar pesas; vienes a compartir el esfuerzo con un equipo que te apoya en cada repetición. Aquí celebramos desde el primer pull-up hasta el último segundo del cronómetro. Si buscas un ambiente divertido, dinámico y que te mantenga motivado, ¡esta es tu clase!",
                    "Lunes 18:00-19:00");
            crossfit.setImageUrl("/img/feature-1.jpg");
            claseRepository.save(crossfit);

            ClassEntity bodyPump = new ClassEntity(
                    "Body Pump",
                    "Entrenamiento con pesas al ritmo de la música que fortalece y tonifica todo el cuerpo. Mejora tu resistencia, quema calorías y gana fuerza en una sola clase. ¡Apta para todos los niveles!",
                    "Viernes 17:00-18:00");
            bodyPump.setImageUrl("/img/feature-2.jpg");
            claseRepository.save(bodyPump);

            ClassEntity spinning = new ClassEntity(
                    "Spinning",
                    "Pedalea al ritmo de la música y quema calorías al máximo. Entrenamiento intenso que mejora tu resistencia, fortalece piernas y te llena de energía. ¡Siente la adrenalina en cada clase!",
                    "Martes 10:00-11:00");
            spinning.setImageUrl("/img/spinning.jpg");
            claseRepository.save(spinning);

            ClassEntity zumba = new ClassEntity(
                    "Zumba",
                    "Baila, quema calorías y diviértete sin darte cuenta. Coreografías fáciles, música latina y buen rollo para ponerte en forma disfrutando. ¡Sonríe, muévete y repite!",
                    "Miércoles 19:00-20:00");
            zumba.setImageUrl("/img/zumba.jpg");
            claseRepository.save(zumba);
        }

        // --- 2. INICIALIZAR SERVICIOS ---
        ServiceEntity fisio = null;

        if (serviceRepository.count() == 0) {
            fisio = new ServiceEntity();
            fisio.setName("Fisioterapia");
            fisio.setDescription("Recupera tu bienestar y mejora tu movilidad con sesiones personalizadas de fisioterapia. Tratamos lesiones, prevenimos dolores y optimizamos tu rendimiento físico mediante técnicas profesionales adaptadas a tus necesidades. Tu cuerpo en buenas manos.");
            fisio.setImageUrl("/img/fisio.jpg");
            fisio.setPrice(39.99); // ✅
            serviceRepository.save(fisio);

            ServiceEntity nutri = new ServiceEntity();
            nutri.setName("Nutrición");
            nutri.setDescription("Alcanza tus objetivos con un plan nutricional hecho a tu medida. Te ayudamos a mejorar tu alimentación, aumentar tu energía y complementar tu entrenamiento con hábitos saludables y sostenibles en el tiempo.");
            nutri.setImageUrl("/img/nutri.jpg");
            nutri.setPrice(29.99); // ✅
            serviceRepository.save(nutri);

            ServiceEntity bebida = new ServiceEntity();
            bebida.setName("Bebidas Extra");
            bebida.setDescription("Disfruta de bebidas saludables y energéticas diseñadas para complementar tu entrenamiento. Hidratación, recuperación y sabor en cada sorbo para que rindas al máximo antes y después de cada sesión.");
            bebida.setImageUrl("/img/bebida.jpg");
            bebida.setPrice(2.99); // ✅
            serviceRepository.save(bebida);

            ServiceEntity spa = new ServiceEntity();
            spa.setName("Spa");
            spa.setDescription("Relájate y recupera tu cuerpo en nuestro espacio de spa. Disfruta de un ambiente pensado para reducir el estrés, aliviar tensiones musculares y favorecer la recuperación física después del entrenamiento.");
            spa.setImageUrl("/img/spa.jpg");
            spa.setPrice(19.99); // ✅
            serviceRepository.save(spa);

        } else {
            fisio = serviceRepository.findByName("Fisioterapia");
        }

        // --- 3. INICIALIZAR USUARIOS ---
        if (userRepository.findByEmail("paco@gmail.com").isEmpty()) {
            User user1 = new User();
            user1.setFirstName("Paco");
            user1.setLastName("García");
            user1.setEmail("paco@gmail.com");
            user1.setPassword(passwordEncoder.encode("1234"));
            user1.setRoles(List.of("USER"));
            user1.setProfileImageUrl("/img/avatar.jpg");

            // ✅ Añade fisioterapia como servicio contratado
            if (fisio != null) {
                user1.getEnrolledServices().add(fisio);
            }

            userRepository.save(user1);
        }

        if (userRepository.findByEmail("admin@titangym.com").isEmpty()) {
            User admin = new User();
            admin.setFirstName("Laura");
            admin.setLastName("Admin");
            admin.setEmail("admin@titangym.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(List.of("ADMIN"));
            admin.setProfileImageUrl("/img/avatar.jpg");
            userRepository.save(admin);
        }
    }
}