package es.codeurjc.proyecto_dws_grupo2.repository;
import java.util.List;
import java.util.Optional;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findTop5ByOrderByIdDesc();
}