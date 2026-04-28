package es.codeurjc.proyecto_dws_grupo2.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Service // ¡Esta etiqueta es clave! Le dice a Spring que esto contiene la lógica de negocio
public class ClassService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    public ClassService(ClassRepository classRepository, UserRepository userRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
    }

    // 1. Obtener todas las clases
    public List<ClassEntity> findAll() {
        return classRepository.findAll();
    }

    // 2. Obtener una clase por su ID
    public Optional<ClassEntity> findById(Long id) {
        return classRepository.findById(id);
    }

    // 3. Apuntar un usuario a una clase
    public void enrollUser(Long classId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ClassEntity clase = classRepository.findById(classId).orElseThrow();
        
        user.getEnrolledClasses().add(clase);
        userRepository.save(user); // Guardamos la relación
    }

    // 4. Desapuntar un usuario de una clase
    public void unenrollUser(Long classId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ClassEntity clase = classRepository.findById(classId).orElseThrow();
        
        user.getEnrolledClasses().remove(clase);
        userRepository.save(user); // Guardamos la relación
    }

    // 5. Comprobar si un usuario está apuntado a una clase
    public boolean isUserEnrolled(Long classId, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        ClassEntity clase = classRepository.findById(classId).orElse(null);
        
        if (user != null && clase != null) {
            return user.getEnrolledClasses().contains(clase);
        }
        return false;
    }
}