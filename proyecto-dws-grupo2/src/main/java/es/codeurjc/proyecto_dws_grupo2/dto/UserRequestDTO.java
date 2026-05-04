package es.codeurjc.proyecto_dws_grupo2.dto;

import es.codeurjc.proyecto_dws_grupo2.model.User;

public record UserRequestDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        String profileImageUrl
) {
    // 2. De DTO a nueva Entidad (Se usa en el POST)
    // Le pasamos la contraseña ya encriptada desde el controlador
    public User toEntity(String encodedPassword) {
        User user = new User();
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setPassword(encodedPassword);
        user.setProfileImageUrl(this.profileImageUrl);
    
        return user;
    }

    // 3. Actualizar Entidad existente con datos del DTO (Se usa en el PUT)
    public void updateEntity(User user, String encodedPassword) {
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        
        if (this.profileImageUrl != null) {
            user.setProfileImageUrl(this.profileImageUrl);
        }
        
        // Solo actualizamos la contraseña si nos enviaron una nueva encriptada
        if (encodedPassword != null) {
            user.setPassword(encodedPassword);
        }
    }
}