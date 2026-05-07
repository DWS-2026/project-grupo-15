package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.service.DocumentService;
import es.codeurjc.proyecto_dws_grupo2.service.ImageService;
import jakarta.validation.Valid;
import es.codeurjc.proyecto_dws_grupo2.model.Document;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Controller
public class ProfileEditController {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final DocumentService documentService;
    private final PasswordEncoder passwordEncoder;

    public ProfileEditController(UserRepository userRepository, ImageService imageService,
            DocumentService documentService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.documentService = documentService;
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
    public String saveProfile(
            @Valid @ModelAttribute("user") User updatedUser,
            BindingResult bindingResult,
            @RequestParam(value = "profilePicture", required = false) MultipartFile imageFile,
            @RequestParam(value = "personalDocument", required = false) MultipartFile personalDocument,
            Principal principal, Model model) throws IOException {

        String email = principal.getName();
        User dbUser = userRepository.findByEmail(email).orElseThrow();

        // --- 1. VALIDACIÓN ---
        boolean hasRealErrors = false;
        for (FieldError error : bindingResult.getFieldErrors()) {
            if (error.getField().equals("password") && (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty())) {
                continue;
            }
            model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            hasRealErrors = true;
        }

        if (hasRealErrors) {
            updatedUser.setProfileImage(dbUser.getProfileImage());
            updatedUser.setDocument(dbUser.getDocument());
            model.addAttribute("user", updatedUser);
            return "profile-edit";
        }

        // --- 2. ACTUALIZAMOS DATOS ---
        dbUser.setFirstName(updatedUser.getFirstName());
        dbUser.setLastName(updatedUser.getLastName());
        dbUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            dbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // --- 3. LÓGICA DE IMAGEN Y DOCUMENTO ---
        if (imageFile != null && !imageFile.isEmpty()) {
            if (dbUser.getProfileImage() != null) {
                Long oldImageId = dbUser.getProfileImage().getId();
                dbUser.setProfileImage(null);
                userRepository.save(dbUser);
                imageService.deleteImage(oldImageId);
            }
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            dbUser.setProfileImage(savedImage);
        }

        if (personalDocument != null && !personalDocument.isEmpty()) {
            // Si ya tenía documento, borramos el antiguo para no dejar huérfanos
            if (dbUser.getDocument() != null) {
                Long oldDocId = dbUser.getDocument().getId();
                dbUser.setDocument(null);
                userRepository.save(dbUser); // Desvinculamos antes de borrar
                documentService.deleteDocument(oldDocId);
            }
            Document doc = documentService.saveDocument(personalDocument);
            dbUser.setDocument(doc);
        }

        // --- 4. GUARDAR ---
        userRepository.save(dbUser);

        // --- 5. ACTUALIZAR SESIÓN DE SPRING SECURITY ---
        if (!email.equals(updatedUser.getEmail())) {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser.getEmail(),
                    currentAuth.getCredentials(),
                    currentAuth.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/profile";
    }
}