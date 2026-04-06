package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.security.Principal;
import org.springframework.security.crypto.password.PasswordEncoder;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProfileEditController {

    private final UserRepository userRepository;
    private static final Path IMAGES_FOLDER = Paths.get("profile_images");

    private final PasswordEncoder passwordEncoder;

    public ProfileEditController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile/edit")
    public String editProfile(Principal principal, Model model) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(User updatedUser,
            @RequestParam("profilePicture") MultipartFile image,
            Principal principal) throws IOException {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String hash = passwordEncoder.encode(updatedUser.getPassword());
            user.setPassword(hash);
        }

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(IMAGES_FOLDER);
            Path imagePath = IMAGES_FOLDER.resolve("user_" + user.getId() + ".jpg");
            image.transferTo(imagePath);
            user.setProfileImageUrl("/profile/image/" + user.getId());
        }

        userRepository.save(user);

        return "redirect:/profile";
    }

    @GetMapping("/profile/image/{id}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable Long id) throws IOException {

        Path imagePath = IMAGES_FOLDER.resolve("user_" + id + ".jpg");
        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}