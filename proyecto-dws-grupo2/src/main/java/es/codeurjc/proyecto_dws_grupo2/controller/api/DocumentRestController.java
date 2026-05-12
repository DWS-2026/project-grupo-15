package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.proyecto_dws_grupo2.model.Document;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.DocumentService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/users")
public class DocumentRestController {

    private final DocumentService documentService;
    private final UserService userService;

    public DocumentRestController(DocumentService documentService, UserService userService) {
        this.documentService = documentService;
        this.userService = userService;
    }

    // POST: Upload document linked to the authenticated user
    @PostMapping("/me/document")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) throws IOException {

        // 🛡️ Safety check: if Spring Security lets the request through without a token, we block it here
        if (userDetails == null) {
            return ResponseEntity.status(401).body(java.util.Map.of(
                "error", "Not authenticated",
                "message", "Session token is missing or has expired. Please log in again."
            ));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("The file cannot be empty");
        }

        // Safe to retrieve the user at this point
        String email = userDetails.getUsername();
        User user = userService.findByEmailOrThrow(email);

        Document doc = documentService.saveDocument(file);
        user.setDocument(doc);
        userService.saveUser(user);

        URI location = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(doc.getId()).toUri();
        return ResponseEntity.created(location).body(
            java.util.Map.of(
                "id", doc.getId(),
                "originalFileName", doc.getOriginalFileName(),
                "contentType", doc.getContentType()
            )
        );
    }

    // GET: Download/view document of the authenticated user
    @GetMapping("/me/document")
    public ResponseEntity<Resource> getMyDocument(Principal principal) throws IOException {

        String email = principal.getName();
        User user = userService.findByEmailOrThrow(email);

        if (user.getDocument() == null) {
            return ResponseEntity.notFound().build();
        }

        Document doc = user.getDocument();
        Resource resource = documentService.loadDocumentAsResource(doc.getId());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, doc.getContentType())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + doc.getOriginalFileName() + "\"")
            .body(resource);
    }

    // GET: Download document by user ID (ADMIN only)
    @GetMapping("/{userId}/document")
    public ResponseEntity<Resource> getDocumentByUserId(
            @PathVariable Long userId) throws IOException {

        User user = userService.getUserById(userId).orElseThrow();

        if (user.getDocument() == null) {
            return ResponseEntity.notFound().build();
        }

        Document doc = user.getDocument();
        Resource resource = documentService.loadDocumentAsResource(doc.getId());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, doc.getContentType())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + doc.getOriginalFileName() + "\"")
            .body(resource);
    }

    // DELETE: Remove document of the authenticated user
    @DeleteMapping("/me/document")
    public ResponseEntity<?> deleteDocument(Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmailOrThrow(email);

        if (user.getDocument() == null) {
            return ResponseEntity.notFound().build();
        }

        user.setDocument(null);
        userService.saveUser(user);

        return ResponseEntity.ok().body(java.util.Map.of("message", "Document deleted successfully"));
    }
}
