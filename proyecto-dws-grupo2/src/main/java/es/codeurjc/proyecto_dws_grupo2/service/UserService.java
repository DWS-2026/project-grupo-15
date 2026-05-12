package es.codeurjc.proyecto_dws_grupo2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public long countUsers() {
        return userRepository.count();
    }

    public List<User> getLatestUsers() {
        return userRepository.findTop5ByOrderByIdDesc();
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

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    
   @Transactional
public void deleteUser(Long id) {
    User user = userRepository.findById(id).orElse(null);
    if (user != null) {
        
        user.getEnrolledClasses().clear();
        user.getEnrolledServices().clear();
        
        
        userRepository.save(user);
        
        
        userRepository.delete(user);
    }
}

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow();
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
