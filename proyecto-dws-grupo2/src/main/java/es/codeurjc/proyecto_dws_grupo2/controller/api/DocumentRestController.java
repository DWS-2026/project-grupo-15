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
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.service.DocumentService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/users")
public class DocumentRestController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    public DocumentRestController(DocumentService documentService, UserRepository userRepository) {
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    // POST: Subir documento ligado al usuario autenticado
    @PostMapping("/me/document")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El fichero no puede estar vacío");
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Document doc = documentService.saveDocument(file);
        user.setDocument(doc);
        userRepository.save(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(doc.getId()).toUri();
        return ResponseEntity.created(location).body(
            java.util.Map.of(
                "id", doc.getId(),
                "originalFileName", doc.getOriginalFileName(),
                "contentType", doc.getContentType()
            )
        );
    }

    // GET: Descargar/visualizar documento del usuario autenticado
    @GetMapping("/me/document")
    public ResponseEntity<Resource> getMyDocument(Principal principal) throws IOException {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

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

    // GET: Descargar documento por ID (solo ADMIN)
    @GetMapping("/{userId}/document")
    public ResponseEntity<Resource> getDocumentByUserId(
            @PathVariable Long userId) throws IOException {

        User user = userRepository.findById(userId).orElseThrow();

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

    // DELETE: Eliminar documento del usuario autenticado
    @DeleteMapping("/me/document")
    public ResponseEntity<?> deleteDocument(Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getDocument() == null) {
            return ResponseEntity.notFound().build();
        }

        user.setDocument(null);
        userRepository.save(user);

        return ResponseEntity.ok().body(java.util.Map.of("message", "Documento eliminado correctamente"));
    }
}