package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPanel(HttpSession session, Model model) {
        return "admin";
    }

    @GetMapping("/admin/members")
    public String adminMembers(HttpSession session, Model model) {
        return "admin-members";
    }

    @GetMapping("/admin/classes")
    public String adminClasses(HttpSession session, Model model) {
        return "admin-classes";
    }

    @GetMapping("/admin/services")
    public String adminServices(HttpSession session, Model model) {
        return "admin-services";
    }

    @GetMapping("/admin/reviews")
    public String adminReviews(HttpSession session, Model model) {
        return "admin-reviews";
    }

    @GetMapping("/admin/profile")
    public String adminProfile(HttpSession session, Model model) {
        return "admin-settings";
    }
}