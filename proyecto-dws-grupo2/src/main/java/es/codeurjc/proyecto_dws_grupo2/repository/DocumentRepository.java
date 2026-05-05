package es.codeurjc.proyecto_dws_grupo2.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.proyecto_dws_grupo2.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {}