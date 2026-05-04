package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ImageService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class ImageRestController {

    private final ImageService imageService;
    private final UserService userService;

    public ImageRestController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    // SUBIR IMAGEN
    @PostMapping("/users/{userId}/image")
    public ResponseEntity<Object> uploadProfileImage(
            @PathVariable Long userId, 
            @RequestParam("imageFile") MultipartFile file) throws IOException {
        
        // CORREGIDO: Usamos getUserById en lugar de findById
        Optional<User> userOpt = userService.getUserById(userId); 
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
        }

        User user = userOpt.get();

        // Borramos la imagen antigua si ya tenía una
        if (user.getProfileImage() != null) {
            Long oldImageId = user.getProfileImage().getId();
            user.setProfileImage(null);
            // CORREGIDO: Usamos saveUser en lugar de save
            userService.saveUser(user); 
            imageService.deleteImage(oldImageId);
        }

        // Guardamos la nueva imagen usando el InputStream
        Image savedImage = imageService.createImage(file.getInputStream());

        // Se la ponemos al usuario
        user.setProfileImage(savedImage);
        // CORREGIDO: Usamos saveUser en lugar de save
        userService.saveUser(user);

        // Creamos la URL para que Postman sepa dónde verla
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/{id}/media")
                .buildAndExpand(savedImage.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // VER IMAGEN
    @GetMapping("/images/{id}/media")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        
        byte[] imageBytes = imageService.getImageFile(id);
        
        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }

        // Devolvemos el byte[] directo a la pantalla
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE) 
                .body(imageBytes);
    }
}