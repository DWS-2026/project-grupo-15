package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // PARA EL MENSAJE EMERGENTE

import java.util.Optional;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository; // AÑADIDO
import jakarta.servlet.http.HttpSession;

@Controller
public class ClassController {

    private final ClassRepository classRepository;
    private final UserRepository userRepository; // AÑADIDO

    // AÑADIDO: Actualizamos el constructor para incluir el userRepository
    public ClassController(ClassRepository classRepository, UserRepository userRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/classes")
    public String mostrarClases(Model model) {
        model.addAttribute("clases", classRepository.findAll());
        return "class-list"; 
    }

    @GetMapping("/classes/{id}")
    public String verDetalleClase(@PathVariable Long id, Model model) {
        Optional<ClassEntity> classOptional = classRepository.findById(id);
        if (classOptional.isPresent()) {
            model.addAttribute("clase", classOptional.get());
            return "class-detail"; 
        } else {
            return "redirect:/classes"; 
        }
    }

    // --- NUEVO MÉTODO: EL BOTÓN DE APUNTARSE ---
    @PostMapping("/classes/{id}/signup")
    public String apuntarseAClase(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
        
        // 1. Comprobamos quién es el usuario que ha iniciado sesión
        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        
        // Si no hay nadie logueado, le echamos al login
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // 2. Buscamos la clase a la que se quiere apuntar
        Optional<ClassEntity> classOptional = classRepository.findById(id);

        if (classOptional.isPresent()) {
            ClassEntity clase = classOptional.get();

            // 3. ¡Magia! Añadimos la clase a la lista del usuario
            usuarioLogado.getEnrolledClasses().add(clase);
            
            // 4. Guardamos el usuario actualizado en la base de datos
            userRepository.save(usuarioLogado);
            
            // 5. Actualizamos la memoria temporal por si acaso
            session.setAttribute("usuarioLogado", usuarioLogado);

            // 6. Preparamos el mensaje Pop-up de éxito
            attributes.addFlashAttribute("mensajeExito", "¡Genial! Te has apuntado a " + clase.getName() + " el " + clase.getSchedule() + ".");
        }

        // 7. Le devolvemos a la lista de clases
        return "redirect:/classes";
    }
}