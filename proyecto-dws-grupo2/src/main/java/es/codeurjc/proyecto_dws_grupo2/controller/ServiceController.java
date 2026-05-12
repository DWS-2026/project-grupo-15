package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.dto.ServiceResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;

@Controller
public class ServiceController {

    private final UserService userService;
    private final ServiceService serviceService;

    public ServiceController(UserService userService, ServiceService serviceService) {
        this.userService = userService;
        this.serviceService = serviceService;
    }

    @GetMapping("/services")
    public String showServices(Model model, Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());

        List<ServiceResponseDTO> allServices = serviceService.findAll().stream()
                .map(service -> new ServiceResponseDTO(service, user.getEnrolledServices().contains(service)))
                .collect(Collectors.toList());

        model.addAttribute("allServices", allServices);
        model.addAttribute("usuarioSesion", user);
        return "services";
    }

    @GetMapping("/extrapayment")
    public String showPaymentPage(@RequestParam Long serviceId, Model model, Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());
        ServiceEntity service = serviceService.findById(serviceId).orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("serviceName", service.getName());
        model.addAttribute("servicePrice", service.getPrice());
        model.addAttribute("serviceId", service.getId());
        model.addAttribute("total", service.getPrice());
        model.addAttribute("origin", "services");
        model.addAttribute("fromRegister", false);

        return "payment";
    }

    @GetMapping("/unsubscribe")
    public String unsubscribe(@RequestParam Long serviceId, Principal principal) {
        serviceService.unsubscribeUser(serviceId, principal.getName());
        return "redirect:/services";
    }
}
