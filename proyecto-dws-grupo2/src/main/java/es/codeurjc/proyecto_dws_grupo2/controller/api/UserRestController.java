package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.security.Principal;
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

// --- Imports de Swagger / OpenAPI ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Gestión de los miembros y perfiles del gimnasio")
public class UserRestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // 1. OBTENER TODOS LOS USUARIOS (Paginado)
    // ==========================================
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una página con la lista de todos los miembros registrados. Requiere permisos de administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada con éxito")
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userRepository.findAll(pageable).map(UserResponseDTO::new);
        return ResponseEntity.ok(users);
    }

    // ==========================================
    // 2. OBTENER PERFIL DEL USUARIO ACTUAL
    // ==========================================
    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del usuario que ha iniciado sesión actualmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado / No hay sesión activa", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 3. OBTENER USUARIO POR ID
    // ==========================================
    @Operation(summary = "Obtener usuario por ID", description = "Busca la información de un miembro específico mediante su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe ningún usuario con ese ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 4. CREAR UN NUEVO USUARIO
    // ==========================================
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {
        User user = new User();
        userDTO.updateEntity(user, passwordEncoder.encode(userDTO.password()));
        User saved = userRepository.save(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(new UserResponseDTO(saved));
    }

    // ==========================================
    // 5. ACTUALIZAR UN USUARIO
    // ==========================================
    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos de un miembro existente. Si se envía una contraseña, será encriptada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO userDTO,
            @RequestAttribute("user") User currentUser) {

        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        if (!currentUser.getId().equals(id) && !isAdmin)
            return ResponseEntity.status(403).build();

        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existing.get();

        String encodedPassword = null;
        if (userDTO.password() != null && !userDTO.password().isBlank()) {
            encodedPassword = passwordEncoder.encode(userDTO.password());
        }

        userDTO.updateEntity(user, encodedPassword);

        User saved = userRepository.save(user);
        return ResponseEntity.ok(new UserResponseDTO(saved));
    }

    // ==========================================
    // 6. ELIMINAR UN USUARIO
    // ==========================================
    @Operation(summary = "Eliminar un usuario", description = "Borra permanentemente la cuenta de un miembro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
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