package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.User;

public record UserRequestDTO(
        String firstName,
        String lastName,
        String email,
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