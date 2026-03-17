    package es.codeurjc.proyecto_dws_grupo2.controller;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;

    import es.codeurjc.proyecto_dws_grupo2.model.User;
    import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

    @Controller
    public class ProfileController {

        private final UserRepository userRepository;

        public ProfileController(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @GetMapping("/profile")
        public String profile(Model model) {

            // TEMPORAL: usuario con ID 1 hasta que tengas login real
            User user = userRepository.findById(1L).orElse(null);

            model.addAttribute("user", user);

            return "profile"; // profile.mustache
        }
    }
