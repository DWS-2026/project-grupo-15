package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.dto.UserResponseDTO;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET: Leer TODOS los usuarios de forma segura
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        // 1. Buscamos todos los usuarios en la Base de Datos
        List<User> users = userRepository.findAll();
        
        // 2. Usamos el "molde" para transformarlos uno a uno
        return users.stream()
                .map(user -> new UserResponseDTO(user))
                .collect(Collectors.toList());
    }

    // GET: Leer UN usuario por su ID de forma segura
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                // Si lo encuentra, lo pasa por el molde UserRgiesponseDTO
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}