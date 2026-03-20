package es.codeurjc.proyecto_dws_grupo2.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
}