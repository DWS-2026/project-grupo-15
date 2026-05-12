package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.dto.PaymentDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PaymentController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final PasswordEncoder passwordEncoder;

    public PaymentController(UserService userService, ServiceService serviceService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/payment_success")
    public String pagoExitoso(
            @Valid @ModelAttribute PaymentDTO paymentData,
            BindingResult bindingResult,
            @RequestParam String origin,
            @RequestParam(required = false) Long serviceId,
            HttpSession session, Principal principal, Model model) {

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }

            model.addAttribute("origin", origin);
            if ("register".equals(origin)) {
                User user = (User) session.getAttribute("usuarioPendiente");
                model.addAttribute("user", user);
                model.addAttribute("fromRegister", true);
                double total = 29.99;
                if (Boolean.TRUE.equals(session.getAttribute("seleccionPhysio"))) {
                    total += 39.99;
                    model.addAttribute("selPhysio", true);
                }
                if (Boolean.TRUE.equals(session.getAttribute("seleccionNutrition"))) {
                    total += 29.99;
                    model.addAttribute("selNutrition", true);
                }
                if (Boolean.TRUE.equals(session.getAttribute("seleccionDrinks"))) {
                    total += 2.99;
                    model.addAttribute("selDrinks", true);
                }
                model.addAttribute("total", total);
            } else {
                ServiceEntity service = serviceService.findById(serviceId).orElseThrow();
                model.addAttribute("fromRegister", false);
                model.addAttribute("serviceName", service.getName());
                model.addAttribute("servicePrice", service.getPrice());
                model.addAttribute("total", service.getPrice());
                model.addAttribute("serviceId", serviceId);
            }
            return "payment";
        }

        if ("register".equals(origin)) {
            User user = (User) session.getAttribute("usuarioPendiente");
            if (user == null) {
                return "redirect:/register";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            serviceService.addSelectedServices(
                    user,
                    Boolean.TRUE.equals(session.getAttribute("seleccionPhysio")),
                    Boolean.TRUE.equals(session.getAttribute("seleccionNutrition")),
                    Boolean.TRUE.equals(session.getAttribute("seleccionDrinks")));

            userService.saveUser(user);
            session.invalidate();

            model.addAttribute("user", user);
            return "successful";
        }

        if (principal == null) {
            return "redirect:/login";
        }

        if (serviceId != null) {
            serviceService.enrollUser(serviceId, principal.getName());
        }

        return "redirect:/services";
    }
}
