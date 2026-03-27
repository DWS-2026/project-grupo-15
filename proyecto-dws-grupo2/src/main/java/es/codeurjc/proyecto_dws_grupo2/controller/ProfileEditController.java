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

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProfileEditController {

    private final UserRepository userRepository;
    private static final Path IMAGES_FOLDER = Paths.get("profile_images");

    public ProfileEditController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(User updatedUser, 
                              @RequestParam("profilePicture") MultipartFile image, 
                              HttpSession session) throws IOException {
        
        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null) {
            return "redirect:/login";
        }

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(IMAGES_FOLDER);
            Path imagePath = IMAGES_FOLDER.resolve("user_" + user.getId() + ".jpg");
            image.transferTo(imagePath);
            user.setProfileImageUrl("user_" + user.getId() + ".jpg");
        }

        userRepository.save(user);
        session.setAttribute("usuarioLogado", user);

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