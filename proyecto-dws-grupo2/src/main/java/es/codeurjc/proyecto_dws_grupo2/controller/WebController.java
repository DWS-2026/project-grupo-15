package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("users", userService.getAllUsers()); // opcional
        return "index";
    }

    // Panel admin
    @GetMapping("/admin-clases")
    public String adminClases() {
        return "admin-clases";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/pagar")
    public String registerUser(Model model, User user, HttpSession session) {

        double totalNum = 29.99;
        if (user.isExtraFisio())
            totalNum += 39.99;
        if (user.isExtraNutricion())
            totalNum += 29.99;
        if (user.isExtraBebidas())
            totalNum += 2.99;

        session.setAttribute("usuarioLogado", user);
        session.setAttribute("total", totalNum);

        model.addAttribute("user", user);
        model.addAttribute("total", totalNum);

        return "payment";
    }

    @PostMapping("/pago-exitoso")
    public String procesarPago(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogado");

        userService.saveUser(user);

        model.addAttribute("user", user);
        return "successful";
    }

    @PostMapping("/perfil")
    public String perfil(Model model, User user) {
        model.addAttribute("user", user);
        return "perfil";
    }

}