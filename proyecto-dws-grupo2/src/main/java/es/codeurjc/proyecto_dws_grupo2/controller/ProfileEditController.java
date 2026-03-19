package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Controller
public class ProfileEditController {

    private final UserRepository userRepository;

    public ProfileEditController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        User user = userRepository.findById(1L).orElse(null);
        model.addAttribute("user", user);
        return "perfil-editar"; //profile-edit.mustache
    }

    @PostMapping("/profile/edit")
    public String saveProfile(User updatedUser) {

        User user = userRepository.findById(1L).orElse(null);

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        if (!updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }

        userRepository.save(user);

        return "redirect:/profile";
    }
}
