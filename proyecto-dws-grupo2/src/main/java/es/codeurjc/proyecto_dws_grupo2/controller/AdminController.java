package es.codeurjc.proyecto_dws_grupo2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.service.ImageService;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ClassRepository classRepository;
    private final ServiceRepository serviceRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService; // Nuestro superhéroe de las imágenes

    public AdminController(UserRepository userRepository, ClassRepository classRepository,
            ServiceRepository serviceRepository, UserService userService,
            ReviewRepository reviewRepository, PasswordEncoder passwordEncoder,
            ImageService imageService) { 
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
    }

    // --- PANEL DASHBOARD ---
    @GetMapping("/admin")
    public String adminPanel(Model model) {
        model.addAttribute("totalMembers", userRepository.count());
        model.addAttribute("totalClasses", classRepository.count());

        List<User> ultimosUsuarios = userRepository.findTop5ByOrderByIdDesc();
        List<Map<String, Object>> recentMembers = new ArrayList<>();

        for (User u : ultimosUsuarios) {
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", u.getFirstName() + " " + u.getLastName());
            map.put("email", u.getEmail());
            map.put("statusColor", "text-success");
            map.put("statusText", "Active");
            recentMembers.add(map);
        }

        model.addAttribute("recentMembers", recentMembers);
        return "admin";
    }

    // --- GESTIÓN DE MIEMBROS ---
    @GetMapping("/admin/members")
    public String adminMembers(Model model) {
        List<User> allMembers = userRepository.findAll();
        List<Map<String, Object>> members = new ArrayList<>();

        for (User u : allMembers) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            if (u.getProfileImage() != null) {
                map.put("photo", "/api/v1/images/" + u.getProfileImage().getId() + "/media");
            } else {
                map.put("photo", "/img/avatar.jpg");
            }
            map.put("nombre", u.getFirstName() + " " + u.getLastName());
            map.put("email", u.getEmail());
            map.put("estado", "Activo");
            map.put("statusColor", "text-success");
            members.add(map);
        }
        model.addAttribute("members", members);
        return "admin-members";
    }

    @GetMapping("/admin/members/{id}")
    public String viewMemberDetail(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/members";

        model.addAttribute("user", user);
        model.addAttribute("classes", user.getEnrolledClasses());
        model.addAttribute("services", user.getEnrolledServices());
        model.addAttribute("reviews", user.getReviews());
        model.addAttribute("hasPhysio", hasService(user, "Fisioterapia"));
        model.addAttribute("hasNutrition", hasService(user, "Nutrición"));
        model.addAttribute("hasDrinks", hasService(user, "Bebidas Extra"));

        return "admin-usuario-detalle";
    }

    @GetMapping("/admin/members/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("hasPhysio", hasService(user, "Fisioterapia"));
        model.addAttribute("hasNutrition", hasService(user, "Nutrición"));
        model.addAttribute("hasDrinks", hasService(user, "Bebidas Extra"));
        return "admin-usuario-edit";
    }

    @PostMapping("/admin/members/edit/{id}")
    public String updateUser(@PathVariable Long id,
            @Valid @ModelAttribute("user") User formUser,
            BindingResult bindingResult, // ⚠️ Recuerda: siempre detrás del objeto validado
            @RequestParam(required = false) String newPassword,
            @RequestParam(defaultValue = "false") boolean extraPhysio,
            @RequestParam(defaultValue = "false") boolean extraNutrition,
            @RequestParam(defaultValue = "false") boolean extraDrinks,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) throws IOException { 

        User dbUser = userRepository.findById(id).orElseThrow();

        // --- 1. VALIDACIÓN ---
        boolean hasRealErrors = false;
        for (FieldError error : bindingResult.getFieldErrors()) {
            // Ignoramos la contraseña porque el admin puede dejarla en blanco si no quiere cambiarla
            if (error.getField().equals("password")) {
                continue; 
            }
            // Mapeamos firstname y lastname a firstNameError y lastNameError (cuidado con las mayúsculas)
            model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            hasRealErrors = true;
        }

        if (hasRealErrors) {
            // Si hay errores, reconstruimos la vista para que Mustache pinte todo bien
            formUser.setId(id);
            formUser.setProfileImage(dbUser.getProfileImage());
            model.addAttribute("user", formUser);
            model.addAttribute("hasPhysio", extraPhysio);
            model.addAttribute("hasNutrition", extraNutrition);
            model.addAttribute("hasDrinks", extraDrinks);
            return "admin-usuario-edit"; // Devolvemos el HTML con los fallos pintados
        }

        // --- 2. ACTUALIZACIÓN (Si no hay errores) ---
        dbUser.setFirstName(formUser.getFirstName());
        dbUser.setLastName(formUser.getLastName());
        dbUser.setEmail(formUser.getEmail());

        // Lógica de servicios extras
        dbUser.getEnrolledServices().clear();
        if (extraPhysio) {
            ServiceEntity s = serviceRepository.findByName("Fisioterapia");
            if (s != null) dbUser.getEnrolledServices().add(s);
        }
        if (extraNutrition) {
            ServiceEntity s = serviceRepository.findByName("Nutrición");
            if (s != null) dbUser.getEnrolledServices().add(s);
        }
        if (extraDrinks) {
            ServiceEntity s = serviceRepository.findByName("Bebidas Extra");
            if (s != null) dbUser.getEnrolledServices().add(s);
        }

        // Si el admin escribió algo en nueva contraseña, se actualiza
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            // Validamos a mano que tenga mínimo 8 caracteres ya que no es el campo principal
            if (newPassword.length() < 8) {
                model.addAttribute("newPasswordError", "La contraseña debe tener al menos 8 caracteres");
                formUser.setId(id);
                formUser.setProfileImage(dbUser.getProfileImage());
                model.addAttribute("user", formUser);
                return "admin-usuario-edit";
            }
            dbUser.setPassword(passwordEncoder.encode(newPassword));
        }

        // Lógica de imágenes
        if (imageFile != null && !imageFile.isEmpty()) {
            if (dbUser.getProfileImage() != null) {
                Long oldImageId = dbUser.getProfileImage().getId();
                dbUser.setProfileImage(null);
                userRepository.save(dbUser); 
                imageService.deleteImage(oldImageId);
            }
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            dbUser.setProfileImage(savedImage);
        }

        userRepository.save(dbUser);
        return "redirect:/admin/members";
    }

    @PostMapping("/admin/members/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/members";
    }

    // --- GESTIÓN DE CLASES ---
    @GetMapping("/admin/classes")
    public String adminClasses(Model model) {
        model.addAttribute("classes", classRepository.findAll());
        return "admin-classes";
    }

    @GetMapping("/admin/classes/new")
    public String showAddClassForm() {
        return "admin-class-create";
    }

    @PostMapping("/admin/classes/new")
    public String addClass(@RequestParam String name,
            @RequestParam String description,
            @RequestParam("classPicture") MultipartFile imageFile) throws IOException {

        ClassEntity newClass = new ClassEntity(name, description, "Horario a definir");
        
        // Guardamos la foto en BBDD si la suben
        if (imageFile != null && !imageFile.isEmpty()) {
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            newClass.setImage(savedImage);
        }

        classRepository.save(newClass);
        return "redirect:/admin/classes";
    }

    @GetMapping("/admin/classes/edit/{id}")
    public String editClass(@PathVariable Long id, Model model) {
        ClassEntity clase = classRepository.findById(id).orElse(null);
        if (clase == null) return "redirect:/admin/classes";
        model.addAttribute("clase", clase);
        return "admin-class-edit";
    }

    @PostMapping("/admin/classes/edit/{id}")
    public String saveClass(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String schedule,
            @RequestParam(value = "classPicture", required = false) MultipartFile imageFile) throws IOException { 

        ClassEntity clase = classRepository.findById(id).orElseThrow();
        clase.setName(name);
        clase.setDescription(description);
        clase.setSchedule(schedule);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (clase.getImage() != null) {
                Long oldImageId = clase.getImage().getId();
                clase.setImage(null);
                classRepository.save(clase);
                imageService.deleteImage(oldImageId);
            }
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            clase.setImage(savedImage);
        }

        classRepository.save(clase);
        return "redirect:/admin/classes";
    }

    @PostMapping("/admin/classes/delete/{id}")
    public String deleteClass(@PathVariable Long id) {
        classRepository.deleteById(id);
        return "redirect:/admin/classes";
    }

    // --- ASISTENTES DE CLASES ---
    @GetMapping("/admin/classes/{id}/asistentes")
    public String viewClassAttendees(@PathVariable Long id, Model model) {
        ClassEntity clase = classRepository.findById(id).orElse(null);
        if (clase == null) return "redirect:/admin/classes";

        List<User> todosLosUsuarios = userRepository.findAll();
        List<User> asistentes = new ArrayList<>();
        List<User> disponibles = new ArrayList<>();

        for (User u : todosLosUsuarios) {
            if (u.getEnrolledClasses().contains(clase)) {
                asistentes.add(u);
            } else if (!u.getRoles().contains("ADMIN")) {
                disponibles.add(u);
            }
        }

        model.addAttribute("clase", clase);
        model.addAttribute("asistentes", asistentes);
        model.addAttribute("totalAsistentes", asistentes.size());
        model.addAttribute("disponibles", disponibles);

        return "admin-clases-listado";
    }

    @PostMapping("/admin/classes/{id}/asistentes/add")
    public String addAttendee(@PathVariable Long id, @RequestParam Long userId) {
        ClassEntity clase = classRepository.findById(id).orElse(null);
        User usuario = userRepository.findById(userId).orElse(null);

        if (clase != null && usuario != null) {
            if (!usuario.getEnrolledClasses().contains(clase)) { 
                usuario.getEnrolledClasses().add(clase);
                userRepository.save(usuario);
            }
        }
        return "redirect:/admin/classes/" + id + "/asistentes";
    }

    // --- GESTIÓN DE SERVICIOS ---
    @GetMapping("/admin/services")
    public String adminServices(Model model) {
        model.addAttribute("services", serviceRepository.findAll());
        return "admin-services";
    }

    @GetMapping("/admin/services/new")
    public String showAddServiceForm() {
        return "admin-service-create";
    }

    @PostMapping("/admin/services/new")
    public String addService(@RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        ServiceEntity newService = new ServiceEntity();
        newService.setName(name);
        newService.setDescription(description);
        newService.setPrice(price);

        if (imageFile != null && !imageFile.isEmpty()) {
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            newService.setImage(savedImage);
        }

        serviceRepository.save(newService);
        return "redirect:/admin/services";
    }

    @GetMapping("/admin/services/edit/{id}")
    public String editService(@PathVariable Long id, Model model) {
        ServiceEntity service = serviceRepository.findById(id).orElse(null);
        if (service == null) return "redirect:/admin/services";
        model.addAttribute("service", service);
        return "admin-service-edit";
    }

    @PostMapping("/admin/services/edit/{id}")
    public String saveService(@PathVariable Long id,
            ServiceEntity updatedService,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        ServiceEntity service = serviceRepository.findById(id).orElseThrow();
        service.setName(updatedService.getName());
        service.setDescription(updatedService.getDescription());
        service.setPrice(updatedService.getPrice()); 

        if (imageFile != null && !imageFile.isEmpty()) {
            if (service.getImage() != null) {
                Long oldImageId = service.getImage().getId();
                service.setImage(null);
                serviceRepository.save(service);
                imageService.deleteImage(oldImageId);
            }
            Image savedImage = imageService.createImage(imageFile.getInputStream());
            service.setImage(savedImage);
        }

        serviceRepository.save(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/admin/services/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        serviceRepository.deleteById(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/admin/services/{id}/miembros")
    public String viewServiceMembers(@PathVariable Long id, Model model) {
        ServiceEntity service = serviceRepository.findById(id).orElse(null);
        if (service == null) return "redirect:/admin/services";

        List<User> todos = userRepository.findAll();
        List<User> inscritos = todos.stream()
                .filter(u -> u.getEnrolledServices().contains(service))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("service", service);
        model.addAttribute("inscritos", inscritos);
        model.addAttribute("totalInscritos", inscritos.size());
        return "admin-servicio-listado";
    }

    // --- REVIEWS ---
    @GetMapping("/admin/reviews")
    public String adminReviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin-reviews";
    }

    @PostMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }

    // --- PERFIL ADMIN ---
    @GetMapping("/admin/profile")
    public String adminProfile() {
        return "admin-settings";
    }

    // --- MÉTODO AUXILIAR ---
    private boolean hasService(User user, String serviceName) {
        return user.getEnrolledServices().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(serviceName));
    }
}