package es.codeurjc.proyecto_dws_grupo2.controller;

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

    public PaymentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/payment_success")
    public String pagoExitoso(@RequestParam String origin,
                               @RequestParam(required = false) String service,
                               HttpSession session,
                               Model model) {

        if ("register".equals(origin)) {
            User usuarioPendiente = (User) session.getAttribute("usuarioPendiente");
            if (usuarioPendiente == null) return "redirect:/register";

            userRepository.save(usuarioPendiente);
            session.removeAttribute("usuarioPendiente");
            session.setAttribute("usuarioLogado", usuarioPendiente);

            model.addAttribute("user", usuarioPendiente);
            return "successful";
        }

        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null) return "redirect:/login";

        switch (service) {
            case "physio"    -> user.setExtraPhysio(true);
            case "nutrition" -> user.setExtraNutrition(true);
            case "drinks"    -> user.setExtraDrinks(true);
        }

        userRepository.save(user);
        session.setAttribute("usuarioLogado", user);

        return "redirect:/services";
    }
}