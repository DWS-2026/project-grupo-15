package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); 
        return "registro";
    }

    @PostMapping("/register")
    public String pagar(
            @Valid @ModelAttribute("user") User user, 
            BindingResult bindingResult, 
            @RequestParam(defaultValue = "false") boolean extraPhysio,
            @RequestParam(defaultValue = "false") boolean extraNutrition,
            @RequestParam(defaultValue = "false") boolean extraDrinks,
            Model model, 
            HttpSession session) {

        
        if (bindingResult.hasErrors()) {
            // IF ERROR, WE RETURN TO THE REGISTRATION PAGE WITH ERROR MESSAGES
            for (FieldError error : bindingResult.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            
            return "registro"; 
        }
        // -----------------------------------

        double total = 29.99; 

        if (extraPhysio) total += 39.99;
        if (extraNutrition) total += 29.99;
        if (extraDrinks) total += 2.99;

        session.setAttribute("usuarioPendiente", user);
        session.setAttribute("seleccionPhysio", extraPhysio);
        session.setAttribute("seleccionNutrition", extraNutrition);
        session.setAttribute("seleccionDrinks", extraDrinks);

        model.addAttribute("user", user);
        model.addAttribute("total", total);
        model.addAttribute("origin", "register");
        model.addAttribute("fromRegister", true);
        model.addAttribute("serviceKey", "");

        return "payment";
    }
}
