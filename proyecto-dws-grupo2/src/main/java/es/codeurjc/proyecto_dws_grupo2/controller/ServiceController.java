package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import es.codeurjc.proyecto_dws_grupo2.model.User;

@Controller
public class ServiceController {

    @GetMapping("/services")
    public String showServices(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);
        return "services";
    }

    @GetMapping("/extrapayment")
    public String showPayment(@RequestParam String service,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("usuarioLogado");
        if (user == null)
            return "redirect:/login";

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