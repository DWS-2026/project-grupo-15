package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.dto.UserRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.UserResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import jakarta.validation.Valid;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Gestión de los miembros y perfiles del gimnasio")
public class UserRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // 1. GET ALL USERS (Paginated)
    // ==========================================
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una página con la lista de todos los miembros registrados. Requiere permisos de administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada con éxito")
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userService.getAllUsers(pageable).map(UserResponseDTO::new);
        return ResponseEntity.ok(users);
    }

    // ==========================================
    // 2. GET CURRENT USER PROFILE
    // ==========================================
    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del usuario que ha iniciado sesión actualmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado / No hay sesión activa", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(Principal principal) {
        String email = principal.getName();
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 3. GET USER BY ID
    // ==========================================
    @Operation(summary = "Obtener usuario por ID", description = "Busca la información de un miembro específico mediante su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe ningún usuario con ese ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 4. CREATE A NEW USER
    // ==========================================
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userDTO) {
        User user = new User();
        userDTO.updateEntity(user, passwordEncoder.encode(userDTO.password()));
        User saved = userService.saveUser(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(new UserResponseDTO(saved));
    }

    // ==========================================
    // 5. UPDATE A USER
    // ==========================================
    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos de un miembro existente. Si se envía una contraseña, será encriptada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para editar este usuario", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO userDTO,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 1. Get the current user from the DB using the email from the token (userDetails)
        Optional<User> currentUserOpt = userService.getUserByEmail(userDetails.getUsername());
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).build(); // In case the user was deleted
        }
        User currentUser = currentUserOpt.get();

        // 2. Permission check: is the user an admin or editing their own profile?
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        if (!currentUser.getId().equals(id) && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        // 3. Find the user to update
        Optional<User> existing = userService.getUserById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existing.get();

        // 4. Update data and password
        String encodedPassword = null;
        if (userDTO.password() != null && !userDTO.password().isBlank()) {
            encodedPassword = passwordEncoder.encode(userDTO.password());
        }

        userDTO.updateEntity(user, encodedPassword);

        User saved = userService.saveUser(user);
        return ResponseEntity.ok(new UserResponseDTO(saved));
    }

    // ==========================================
    // 6. DELETE A USER
    // ==========================================
    @Operation(summary = "Eliminar un usuario", description = "Borra permanentemente la cuenta de un miembro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);
        return ResponseEntity.ok(new UserResponseDTO(user.get()));
    }
}
