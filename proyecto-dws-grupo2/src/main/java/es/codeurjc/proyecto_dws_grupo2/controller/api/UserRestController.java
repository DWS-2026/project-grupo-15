package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.dto.UserRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.UserResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET: Todos los usuarios
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> pageResult = userRepository.findAll(pageable)
                .map(UserResponseDTO::new);
        
        return ResponseEntity.ok(pageResult);
    }

    // GET: Usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Crear usuario
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {
        // 1. Encriptamos la contraseña extraída del DTO
        String encodedPassword = passwordEncoder.encode(userDTO.password());
        
        // 2. El DTO nos devuelve la entidad limpia y segura
        User user = userDTO.toEntity(encodedPassword);
        
        User saved = userRepository.save(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(new UserResponseDTO(saved));
    }

    // PUT: Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existing.get();
        
        // Solo encriptamos si el usuario envió una contraseña nueva
        String encodedPassword = null;
        if (userDTO.password() != null && !userDTO.password().isBlank()) {
            encodedPassword = passwordEncoder.encode(userDTO.password());
        }

        userDTO.updateEntity(user, encodedPassword);

        User saved = userRepository.save(user);
        return ResponseEntity.ok(new UserResponseDTO(saved));
    }

    // DELETE: Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        userRepository.deleteById(id);
        return ResponseEntity.ok(new UserResponseDTO(user.get()));
    }
}