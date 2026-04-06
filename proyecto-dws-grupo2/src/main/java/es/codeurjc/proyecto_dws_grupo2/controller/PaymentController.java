package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal; // El portero

import org.springframework.security.crypto.password.PasswordEncoder; // El encriptador
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Necesitamos esto para las contraseñas

    // Actualizamos el constructor
    public PaymentController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/payment_success")
    public String pagoExitoso(@RequestParam String origin,
                              @RequestParam(required = false) String service,
                              HttpSession session,
                              Principal principal,
                              Model model) {

        // --- CASO 1: UN USUARIO NUEVO SE REGISTRA ---
        if ("register".equals(origin)) {
            User usuarioPendiente = (User) session.getAttribute("usuarioPendiente");
            
            if (usuarioPendiente == null) {
                return "redirect:/register";
            }

            // ¡MAGIA DE SEGURIDAD! Encriptamos la contraseña antes de guardar
            usuarioPendiente.setPassword(passwordEncoder.encode(usuarioPendiente.getPassword()));
            
            // Si tu entidad User necesita un rol por defecto, se lo pondríamos aquí
            // usuarioPendiente.setRoles(List.of("USER"));

            // Lo guardamos por fin en la base de datos
            userRepository.save(usuarioPendiente);
            
            // Limpiamos la memoria temporal
            session.removeAttribute("usuarioPendiente");

            /* IMPORTANTE: Spring Security no te logea automáticamente solo por guardarte en la BBDD.
               Le pasamos sus datos a la vista "successful", y desde ahí tendrá que ir al /login 
               para entrar por primera vez con su nueva contraseña. */
            model.addAttribute("user", usuarioPendiente);
            return "successful";
        }

        // --- CASO 2: UN USUARIO QUE YA TIENE CUENTA COMPRA UN EXTRA ---
        
        // Si por algún casual no está logeado, lo echamos
        if (principal == null) {
            return "redirect:/login";
        }

        // Buscamos al usuario real usando a Principal
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        
        if (user != null) {
            switch (service) {
                case "physio" -> user.setExtraPhysio(true);
                case "nutrition" -> user.setExtraNutrition(true);
                case "drinks" -> user.setExtraDrinks(true);
            }
            userRepository.save(user); // Guardamos la compra extra
        }

        return "redirect:/services";
    }
}