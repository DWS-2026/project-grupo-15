package es.codeurjc.proyecto_dws_grupo2; // Asegúrate de que el paquete coincida con tus carpetas

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import jakarta.annotation.PostConstruct;

@Controller
public class WebController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-clases")
    public String adminClases() {
        return "admin-clases";
    }

    @GetMapping("/registro")
    public String savePost(Model model, User user)
            throws IOException {
        userRepository.save(user);
        model.addAttribute("user", user);

        double total = 29.99;
        if (user.isExtraFisio())
            total += 39.99;
        if (user.isExtraNutricion())
            total += 29.99;
        if (user.isExtraBebidas())
            total += 2.99;

        model.addAttribute("total", String.format("%.2f", total).replace(".", ","));
        return "payment";
    }

}