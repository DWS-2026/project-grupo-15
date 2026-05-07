package es.codeurjc.proyecto_dws_grupo2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Elimina un usuario en cascada.
     * La anotación @Transactional asegura que si algo falla, no se borre nada.
     * Gracias a CascadeType.ALL y @OnDelete en la entidad User, 
     * se limpiarán roles, reviews y tablas intermedias automáticamente.
     */
   @Transactional
public void deleteUser(Long id) {
    User user = userRepository.findById(id).orElse(null);
    if (user != null) {
        // Limpiamos las asociaciones para romper el vínculo en las tablas intermedias
        // sin eliminar las entidades ClassEntity, Activity o ServiceEntity
        user.getEnrolledClasses().clear();
        user.getEnrolledServices().clear();
        
        // Guardamos para que se limpien las tablas user_classes, etc.
        userRepository.save(user);
        
        // Ahora borramos el usuario. Solo se borrarán en cascada sus Reviews y Roles.
        userRepository.delete(user);
    }
}

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User addProfileImageToUser(long userId, Image image) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setProfileImage(image);
        return userRepository.save(user);
    }

    // Método para quitar la foto de perfil al usuario
    public User removeProfileImageFromUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setProfileImage(null);
        return userRepository.save(user);
    }
}