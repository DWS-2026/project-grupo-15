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

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService;
import es.codeurjc.proyecto_dws_grupo2.service.ImageService;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

// --- Imports de Swagger / OpenAPI ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Imágenes", description = "Gestión de subida y descarga de imágenes del sistema (Usuarios, Clases y Servicios)")
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
    // 1. SUBIR IMAGEN DE USUARIO
    // ==========================================
    @Operation(summary = "Subir foto de perfil de usuario", description = "Sube una imagen y la asocia como foto de perfil de un usuario existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagen subida y asociada con éxito"),
        @ApiResponse(responseCode = "400", description = "El archivo enviado está vacío o es inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PostMapping(value = "/users/{userId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadProfileImage(
            @PathVariable Long userId, 
            @RequestParam("imageFile") MultipartFile file) throws IOException {
        
        Optional<User> userOpt = userService.getUserById(userId); 
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("El archivo no puede estar vacío");

        User user = userOpt.get();

        if (user.getProfileImage() != null) {
            Long oldImageId = user.getProfileImage().getId();
            user.setProfileImage(null);
            userService.saveUser(user); 
            imageService.deleteImage(oldImageId);
        }

        Image savedImage = imageService.createImage(file.getInputStream());
        user.setProfileImage(savedImage);
        userService.saveUser(user);

        URI location = buildImageUri(savedImage.getId());
        return ResponseEntity.created(location).build();
    }

    // ==========================================
    // 2. SUBIR IMAGEN DE CLASE
    // ==========================================
    @Operation(summary = "Subir imagen de una clase", description = "Sube una imagen representativa para una clase o actividad del gimnasio.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagen subida y asociada con éxito"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada", content = @Content)
    })
    @PostMapping(value = "/classes/{classId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadClassImage(
            @PathVariable Long classId, 
            @RequestParam("imageFile") MultipartFile file) throws IOException {
        
        Optional<ClassEntity> classOpt = classService.findById(classId); 
        if (classOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("El archivo no puede estar vacío");

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

        URI location = buildImageUri(savedImage.getId());
        return ResponseEntity.created(location).build();
    }

    // ==========================================
    // 3. SUBIR IMAGEN DE SERVICIO
    // ==========================================
    @Operation(summary = "Subir imagen de un servicio", description = "Sube una imagen representativa para un servicio o suplemento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagen subida y asociada con éxito"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado", content = @Content)
    })
    @PostMapping(value = "/services/{serviceId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadServiceImage(
            @PathVariable Long serviceId, 
            @RequestParam("imageFile") MultipartFile file) throws IOException {
        
        Optional<ServiceEntity> serviceOpt = serviceEntityService.findById(serviceId); 
        if (serviceOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("El archivo no puede estar vacío");

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

        URI location = buildImageUri(savedImage.getId());
        return ResponseEntity.created(location).build();
    }

    // ==========================================
    // 4. DESCARGAR IMAGEN (Universal)
    // ==========================================
    @Operation(summary = "Descargar una imagen", description = "Descarga los datos binarios de una imagen a partir de su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen descargada con éxito"),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada", content = @Content)
    })
    @GetMapping("/images/{id}/media")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        byte[] imageBytes = imageService.getImageFile(id);
        
        if (imageBytes == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE) 
                .body(imageBytes);
    }

    // Método auxiliar (No se documenta porque no tiene mapeo HTTP)
    private URI buildImageUri(Long imageId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/{id}/media")
                .buildAndExpand(imageId)
                .toUri();
    }
}