package es.codeurjc.proyecto_dws_grupo2.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.dto.ServiceResponseDTO; 

@Controller
public class ServiceController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public ServiceController(UserRepository userRepository, ServiceRepository serviceRepository) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/services")
    public String showServices(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        List<ServiceResponseDTO> allServices = serviceRepository.findAll().stream()
                .map(s -> new ServiceResponseDTO(s, user.getEnrolledServices().contains(s)))
                .collect(Collectors.toList());

        model.addAttribute("allServices", allServices);
        model.addAttribute("usuarioSesion", user);
        return "services";
    }

    @GetMapping("/extrapayment")
    public String showPaymentPage(@RequestParam Long serviceId, Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        ServiceEntity s = serviceRepository.findById(serviceId).orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("serviceName", s.getName());
        model.addAttribute("servicePrice", s.getPrice());
        model.addAttribute("serviceId", s.getId());
        model.addAttribute("total", s.getPrice());
        model.addAttribute("origin", "services");
        model.addAttribute("fromRegister", false); 

        return "payment";
    }

    @GetMapping("/unsubscribe")
    public String unsubscribe(@RequestParam Long serviceId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        ServiceEntity s = serviceRepository.findById(serviceId).orElse(null);

        if (s != null) {
            user.getEnrolledServices().remove(s);
            userRepository.save(user);
        }
        return "redirect:/services";
    }
}