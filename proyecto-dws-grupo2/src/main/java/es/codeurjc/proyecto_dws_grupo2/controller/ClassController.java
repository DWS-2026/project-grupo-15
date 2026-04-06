package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import java.security.Principal; // Importamos el nuevo "portero"
import java.util.Optional;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository; 

@Controller
public class ClassController {

    private final ClassRepository classRepository;
    private final UserRepository userRepository; 

    public ClassController(ClassRepository classRepository, UserRepository userRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
    }

    // --- MÉTODO AYUDANTE: Para mantener el menú de navegación funcionando ---
    private void cargarUsuarioEnMenu(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                // Le pasamos el usuario al HTML para que pinte "Hola, Paco" y el botón de Cerrar Sesión
                model.addAttribute("usuarioSesion", user);
            }
        }
    }

    @GetMapping("/classes/info") 
    public String mostrarInfoClases(Model model, Principal principal) {
        cargarUsuarioEnMenu(model, principal); 
        model.addAttribute("clases", classRepository.findAll());
        return "class"; 
    }

    @GetMapping("/classes")
    public String mostrarClases(Model model, Principal principal) {
        cargarUsuarioEnMenu(model, principal); 
        model.addAttribute("clases", classRepository.findAll());
        return "class-list"; 
    }


  @GetMapping("/classes/{id}")
    public String verDetalleClase(@PathVariable Long id, Model model, Principal principal) {
        cargarUsuarioEnMenu(model, principal); 
        
        ClassEntity clase = classRepository.findById(id).orElse(null);
        if (clase == null) return "redirect:/classes";

        model.addAttribute("clase", clase);

        // Lógica para los botones de apuntado
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                // Comprobamos si la clase está en su lista de inscritas
                boolean isEnrolled = user.getEnrolledClasses().contains(clase);
                model.addAttribute("isEnrolled", isEnrolled);
            }
        }

        return "class-detail"; 
    }
    @PostMapping("/classes/{id}/signup")
    public String apuntarseAClase(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
        
   
        User usuarioLogado = userRepository.findByEmail(principal.getName()).orElse(null);

        
        if (usuarioLogado != null) {
            
            
            Optional<ClassEntity> classOptional = classRepository.findById(id);

            if (classOptional.isPresent()) {
                ClassEntity clase = classOptional.get();

                
                usuarioLogado.getEnrolledClasses().add(clase);
                
                
                userRepository.save(usuarioLogado);
                
                
                attributes.addFlashAttribute("mensajeExito", "¡Genial! Te has apuntado a " + clase.getName() + " el " + clase.getSchedule() + ".");
            }
        }

        return "redirect:/classes";
    }

    @PostMapping("/classes/{id}/leave")
    public String desapuntarseDeClase(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        ClassEntity clase = classRepository.findById(id).orElseThrow();

        user.getEnrolledClasses().remove(clase);
        userRepository.save(user);

        return "redirect:/classes/" + id;
    }
}