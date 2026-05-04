package es.codeurjc.proyecto_dws_grupo2.repository;

import es.codeurjc.proyecto_dws_grupo2.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}