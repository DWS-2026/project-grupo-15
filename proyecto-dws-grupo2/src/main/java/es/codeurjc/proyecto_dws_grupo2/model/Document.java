package es.codeurjc.proyecto_dws_grupo2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName; // "contrato_juan.pdf"
    private String contentType; // "application/pdf"

    public Document() {
    }

    public Document(String originalFileName, String contentType) {
        this.originalFileName = originalFileName;
        this.contentType = contentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String n) {
        this.originalFileName = n;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String ct) {
        this.contentType = ct;
    }
}
