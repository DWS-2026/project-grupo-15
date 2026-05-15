package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String firstName,

        @NotBlank(message = "El apellido no puede estar vacío")
        String lastName,

        @Email(message = "El formato del email no es válido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
        String password
) {
   
    // 2. From DTO to new entity
    // We have the encrypted password
    public User toEntity(String encodedPassword) {
        User user = new User();
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setPassword(encodedPassword);
    
        return user;
    }

    // 3. Update entity with data from DTO
    public void updateEntity(User user, String encodedPassword) {
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        
        // Only update if the function sends us a encoded password
        if (encodedPassword != null) {
            user.setPassword(encodedPassword);
        }
    }
}