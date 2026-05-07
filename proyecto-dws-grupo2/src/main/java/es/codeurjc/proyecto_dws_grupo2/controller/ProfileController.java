package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.model.Document;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.service.DocumentService;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import java.security.Principal;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final DocumentService documentService;

    public ProfileController(UserRepository userRepository, DocumentService documentService) {
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
        return "perfil";
    }

    @GetMapping("/profile/document")
    public ResponseEntity<Resource> downloadDocument(Principal principal) throws IOException {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getDocument() == null) {
            return ResponseEntity.notFound().build();
        }

        Long docId = user.getDocument().getId();
        Resource resource = documentService.loadDocumentAsResource(docId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + user.getDocument().getOriginalFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, user.getDocument().getContentType())
                .body(resource);
    }
}