package es.codeurjc.proyecto_dws_grupo2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.proyecto_dws_grupo2.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // consultas personalizadas opcionales
    User findByEmail(String email);
}