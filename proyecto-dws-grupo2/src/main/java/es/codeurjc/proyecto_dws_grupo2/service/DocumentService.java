package es.codeurjc.proyecto_dws_grupo2.service;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.proyecto_dws_grupo2.model.Document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import es.codeurjc.proyecto_dws_grupo2.repository.DocumentRepository;
import java.util.Optional;

@Service
public class DocumentService {

    private final Path uploadDir = Paths.get("uploads/documents");

    @Autowired
    private DocumentRepository documentRepository;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public Document saveDocument(MultipartFile file) throws IOException {
        // 1. Guardamos en BD para obtener el ID
        Document doc = new Document(
            file.getOriginalFilename(),
            file.getContentType()
        );
        documentRepository.save(doc);

        // 2. Guardamos en disco con el ID como nombre interno
        Path filePath = uploadDir.resolve("doc_" + doc.getId());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return doc;
    }

    public Resource loadDocumentAsResource(Long docId) throws IOException {
        Path filePath = uploadDir.resolve("doc_" + docId);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Fichero no encontrado: " + docId);
        }
        return resource;
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }
}
