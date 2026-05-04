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
import es.codeurjc.proyecto_dws_grupo2.service.ServiceEntityService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class ImageRestController {

    private final ImageService imageService;
    private final UserService userService;
    private final ClassService classService; 
    private final ServiceEntityService serviceEntityService; 

    public ImageRestController(ImageService imageService, UserService userService, 
                               ClassService classService, ServiceEntityService serviceEntityService) {
        this.imageService = imageService;
        this.userService = userService;
        this.classService = classService;
        this.serviceEntityService = serviceEntityService;
    }

    // 1. UPLOAD USER IMAGE
    @PostMapping("/users/{userId}/image")
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

    // 2. UPLOAD CLASS IMAGE
    @PostMapping("/classes/{classId}/image")
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

    // 3. UPLOAD SERVICE IMAGE
    @PostMapping("/services/{serviceId}/image")
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

    // 4. DOWNLOAD ANY IMAGE (Universal)
    @GetMapping("/images/{id}/media")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        byte[] imageBytes = imageService.getImageFile(id);
        
        if (imageBytes == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE) 
                .body(imageBytes);
    }

    // Helper method to avoid repeating URI creation code
    private URI buildImageUri(Long imageId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/{id}/media")
                .buildAndExpand(imageId)
                .toUri();
    }
}