package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.service.ImageService;

import java.io.IOException;

@Controller
public class ProfileEditController {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;

    public ProfileEditController(UserRepository userRepository, ImageService imageService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.imageService = imageService;
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
            @RequestParam(value = "profilePicture", required = false) MultipartFile imageFile,
            Principal principal) throws IOException { 

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        // 1. Actualizamos datos básicos
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        // 2. Actualizamos contraseña si se ha rellenado
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // 3. NUEVA LÓGICA DE IMAGEN: Guardar en Base de Datos (byte[])
        if (imageFile != null && !imageFile.isEmpty()) {
            // Si ya tenía imagen, borramos la antigua de la BBDD para no dejar huérfanos
            if (user.getProfileImage() != null) {
                Long oldImageId = user.getProfileImage().getId();
                user.setProfileImage(null);
                userRepository.save(user); // Desvinculamos antes de borrar
                imageService.deleteImage(oldImageId);
            }
            
            // Creamos la nueva imagen en BBDD
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            user.setProfileImage(savedImage);
        }

        userRepository.save(user);

        // 4. ACTUALIZAR SESIÓN (Tu "magia" para que Spring Security se entere del nuevo email)
        if (!email.equals(updatedUser.getEmail())) {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser.getEmail(), 
                    currentAuth.getCredentials(), 
                    currentAuth.getAuthorities()
            );
            
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/profile";
    }
}