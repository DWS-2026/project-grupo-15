package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal; // Importante para la nueva seguridad

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Controller
public class ServiceController {

    // 1. Inyectamos el repositorio para poder buscar en la base de datos
    private final UserRepository userRepository;

    public ServiceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/services")
    public String showServices(Model model, Principal principal) {
        
        // 2. Buscamos al usuario por su email (que es el "nombre" en Principal)
        User user = userRepository.findByEmail(principal.getName()).orElse(null);

        model.addAttribute("user", user);
        return "services";
    }

    @GetMapping("/extrapayment")
    public String showPayment(@RequestParam String service, Model model, Principal principal) {

        // Volvemos a usar Principal para sacar al usuario, sin ifs ni redirecciones manuales
        User user = userRepository.findByEmail(principal.getName()).orElse(null);

        // Tu lógica del switch se queda exactamente igual, ¡está perfecta!
        switch (service) {
            case "physio" -> {
                model.addAttribute("serviceName", "Fisioterapia");
                model.addAttribute("servicePrice", "39,99");
            }
            case "nutrition" -> {
                model.addAttribute("serviceName", "Nutrición");
                model.addAttribute("servicePrice", "29,99");
            }
            case "drinks" -> {
                model.addAttribute("serviceName", "Bebidas Extra");
                model.addAttribute("servicePrice", "2,99");
            }
            default -> {
                return "redirect:/services";
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("serviceKey", service);
        model.addAttribute("origin", "services");
        model.addAttribute("fromRegister", false);
        model.addAttribute("total", model.getAttribute("servicePrice"));

        return "payment";
    }
}