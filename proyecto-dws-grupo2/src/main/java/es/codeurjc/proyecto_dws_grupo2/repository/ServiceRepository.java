package es.codeurjc.proyecto_dws_grupo2.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
}