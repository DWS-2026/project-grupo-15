package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService;
import es.codeurjc.proyecto_dws_grupo2.service.ImageService;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Imágenes", description = "Gestión de subida y descarga de imágenes")
public class ImageRestController {

    private final ImageService imageService;
    private final UserService userService;
    private final ClassService classService; 
    private final ServiceService serviceEntityService; 

    public ImageRestController(ImageService imageService, UserService userService, 
                               ClassService classService, ServiceService serviceEntityService) {
        this.imageService = imageService;
        this.userService = userService;
        this.classService = classService;
        this.serviceEntityService = serviceEntityService;
    }

    // ==========================================
    // 1. SUBIR IMAGEN DE USUARIO (CON PROTECCIÓN IDOR)
    // ==========================================
    @Operation(summary = "Subir foto de perfil de usuario")
    @PostMapping(value = "/users/{userId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadProfileImage(
            @PathVariable Long userId, 
            @RequestParam("imageFile") MultipartFile file,
            Principal principal) throws IOException {
        
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Obtener usuario autenticado desde el token
        Optional<User> currentUserOpt = userService.getUserByEmail(principal.getName());
        if (currentUserOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User currentUser = currentUserOpt.get();

        // Verificación de seguridad: Solo el dueño o el ADMIN
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        if (!currentUser.getId().equals(userId) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<User> targetUserOpt = userService.getUserById(userId); 
        if (targetUserOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("El archivo está vacío");

        User targetUser = targetUserOpt.get();

        // Lógica de reemplazo de imagen
        if (targetUser.getProfileImage() != null) {
            Long oldImageId = targetUser.getProfileImage().getId();
            targetUser.setProfileImage(null);
            userService.saveUser(targetUser); 
            imageService.deleteImage(oldImageId);
        }

        Image savedImage = imageService.createImage(file.getInputStream());
        targetUser.setProfileImage(savedImage);
        userService.saveUser(targetUser);

        return ResponseEntity.created(buildImageUri(savedImage.getId())).build();
    }

    // ==========================================
    // 2. SUBIR IMAGEN DE CLASE (SOLO ADMIN)
    // ==========================================
    @Operation(summary = "Subir imagen de una clase")
    @PostMapping(value = "/classes/{classId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadClassImage(
            @PathVariable Long classId, 
            @RequestParam("imageFile") MultipartFile file,
            Principal principal) throws IOException {
        
        if (!checkIsAdmin(principal)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<ClassEntity> classOpt = classService.findById(classId); 
        if (classOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("El archivo está vacío");

        ClassEntity classEntity = classOpt.get();

        if (classEntity.getImage() != null) {
            Long oldImageId = classEntity.getImage().getId();
            classEntity.setImage(null);
            classService.save(classEntity); 
            imageService.deleteImage(oldImageId);
        }

        Image savedImage = imageService.createImage(file.getInputStream());
        classEntity.setImage(savedImage);
        classService.save(classEntity);

        return ResponseEntity.created(buildImageUri(savedImage.getId())).build();
    }

    // ==========================================
    // 3. SUBIR IMAGEN DE SERVICIO (SOLO ADMIN)
    // ==========================================
    @Operation(summary = "Subir imagen de un servicio")
    @PostMapping(value = "/services/{serviceId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadServiceImage(
            @PathVariable Long serviceId, 
            @RequestParam("imageFile") MultipartFile file,
            Principal principal) throws IOException {
        
        if (!checkIsAdmin(principal)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<ServiceEntity> serviceOpt = serviceEntityService.findById(serviceId); 
        if (serviceOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("El archivo está vacío");

        ServiceEntity service = serviceOpt.get();

        if (service.getImage() != null) {
            Long oldImageId = service.getImage().getId();
            service.setImage(null);
            serviceEntityService.save(service); 
            imageService.deleteImage(oldImageId);
        }

        Image savedImage = imageService.createImage(file.getInputStream());
        service.setImage(savedImage);
        serviceEntityService.save(service);

        return ResponseEntity.created(buildImageUri(savedImage.getId())).build();
    }

    // ==========================================
    // 4. DESCARGAR IMAGEN
    // ==========================================
    @GetMapping("/images/{id}/media")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        byte[] imageBytes = imageService.getImageFile(id);
        if (imageBytes == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE) 
                .body(imageBytes);
    }

    // --- MÉTODOS AUXILIARES ---

    private URI buildImageUri(Long imageId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/{id}/media")
                .buildAndExpand(imageId)
                .toUri();
    }

    private boolean checkIsAdmin(Principal principal) {
        if (principal == null) return false;
        Optional<User> user = userService.getUserByEmail(principal.getName());
        return user.isPresent() && user.get().getRoles().contains("ADMIN");
    }
}