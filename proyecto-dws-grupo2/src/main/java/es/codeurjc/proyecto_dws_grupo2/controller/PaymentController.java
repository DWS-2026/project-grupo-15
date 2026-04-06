package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    public PaymentController(UserRepository userRepository,
            ServiceRepository serviceRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/payment_success")
    public String pagoExitoso(@RequestParam String origin,
            @RequestParam(required = false) Long serviceId, // ✅ Recibe Long ID correctamente
            HttpSession session,
            Principal principal,
            Model model) {

        // --- CASO 1: REGISTRO DE NUEVO USUARIO ---
        if ("register".equals(origin)) {
            User user = (User) session.getAttribute("usuarioPendiente");
            if (user == null)
                return "redirect:/register";

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));

            // Añadir servicios desde las banderas de la sesión
            if (Boolean.TRUE.equals(session.getAttribute("selPhysio")))
                user.getEnrolledServices().add(serviceRepository.findByName("Fisioterapia"));
            if (Boolean.TRUE.equals(session.getAttribute("selNutrition")))
                user.getEnrolledServices().add(serviceRepository.findByName("Nutrición"));
            if (Boolean.TRUE.equals(session.getAttribute("selDrinks")))
                user.getEnrolledServices().add(serviceRepository.findByName("Bebidas Extra"));

            userRepository.save(user);
            session.invalidate(); 

            model.addAttribute("user", user);
            return "successful";
        }

        // --- CASO 2: COMPRA DE EXTRA (USUARIO YA LOGEADO) ---
        if (principal == null)
            return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        if (serviceId != null) {
            ServiceEntity s = serviceRepository.findById(serviceId).orElse(null);
            if (s != null) {
                user.getEnrolledServices().add(s);
                userRepository.save(user);
            }
        }

        return "redirect:/services";
    }
}