package es.codeurjc.proyecto_dws_grupo2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.service.UserService;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ReviewRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ClassRepository classRepository;
    private final ServiceRepository serviceRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Path CLASSES_IMAGES_FOLDER = Paths.get("classes_images");
    private static final Path SERVICES_IMAGES_FOLDER = Paths.get("services_images");
    private static final Path USERS_IMAGES_FOLDER = Paths.get("users_images");

    public AdminController(UserRepository userRepository, ClassRepository classRepository,
            ServiceRepository serviceRepository, UserService userService,
            ReviewRepository reviewRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
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
            map.put("photo", u.getProfileImageUrl());
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
        if (user == null)
            return "redirect:/admin/members";

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
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String newPassword,
            @RequestParam(defaultValue = "false") boolean extraPhysio,
            @RequestParam(defaultValue = "false") boolean extraNutrition,
            @RequestParam(defaultValue = "false") boolean extraDrinks,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        User user = userRepository.findById(id).orElseThrow();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        // ✅ Actualiza servicios desde BD
        user.getEnrolledServices().clear();
        if (extraPhysio) {
            ServiceEntity s = serviceRepository.findByName("Fisioterapia");
            if (s != null)
                user.getEnrolledServices().add(s);
        }
        if (extraNutrition) {
            ServiceEntity s = serviceRepository.findByName("Nutrición");
            if (s != null)
                user.getEnrolledServices().add(s);
        }
        if (extraDrinks) {
            ServiceEntity s = serviceRepository.findByName("Bebidas Extra");
            if (s != null)
                user.getEnrolledServices().add(s);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            Files.createDirectories(USERS_IMAGES_FOLDER);
            Path imagePath = USERS_IMAGES_FOLDER.resolve("user_" + id + ".jpg");
            imageFile.transferTo(imagePath);
            user.setProfileImageUrl("/admin/members/image/" + id);
        }

        userRepository.save(user);
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
            @RequestParam("classPicture") MultipartFile image) throws IOException {

        ClassEntity newClass = new ClassEntity(name, description, "Horario a definir");
        classRepository.save(newClass);

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(CLASSES_IMAGES_FOLDER);
            Path imagePath = CLASSES_IMAGES_FOLDER.resolve("class_" + newClass.getId() + ".jpg");
            image.transferTo(imagePath);
            newClass.setImageUrl("/admin/classes/image/" + newClass.getId());
        } else {
            newClass.setImageUrl("/img/class-default.jpg");
        }

        classRepository.save(newClass);
        return "redirect:/admin/classes";
    }

    @GetMapping("/admin/classes/edit/{id}")
    public String editClass(@PathVariable Long id, Model model) {
        ClassEntity clase = classRepository.findById(id).orElse(null);
        if (clase == null)
            return "redirect:/admin/classes";
        model.addAttribute("clase", clase);
        return "admin-class-edit";
    }

    // ✅ AÑADIDO: guardar cambios de clase
    @PostMapping("/admin/classes/edit/{id}")
    public String saveClass(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String schedule,
            @RequestParam(value = "classPicture", required = false) MultipartFile image) throws IOException { // ✅
                                                                                                              // ¡Magia
                                                                                                              // aplicada
                                                                                                              // aquí!

        ClassEntity clase = classRepository.findById(id).orElseThrow();
        clase.setName(name);
        clase.setDescription(description);
        clase.setSchedule(schedule);

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(CLASSES_IMAGES_FOLDER);
            Path imagePath = CLASSES_IMAGES_FOLDER.resolve("class_" + id + ".jpg");
            image.transferTo(imagePath);
            clase.setImageUrl("/admin/classes/image/" + id);
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
        if (clase == null)
            return "redirect:/admin/classes";

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
            if (!usuario.getEnrolledClasses().contains(clase)) { // ✅ evita duplicados
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

    // ✅ AÑADIDO: mostrar formulario de edición de servicio
    @GetMapping("/admin/services/edit/{id}")
    public String editService(@PathVariable Long id, Model model) {
        ServiceEntity service = serviceRepository.findById(id).orElse(null);
        if (service == null)
            return "redirect:/admin/services";
        model.addAttribute("service", service);
        return "admin-service-edit";
    }

    @PostMapping("/admin/services/edit/{id}")
    public String saveService(@PathVariable Long id,
            ServiceEntity updatedService,
            @RequestParam("imageFile") MultipartFile image) throws IOException {

        ServiceEntity service = serviceRepository.findById(id).orElseThrow();
        service.setName(updatedService.getName());
        service.setDescription(updatedService.getDescription());
        service.setPrice(updatedService.getPrice()); // ✅ también actualiza el precio

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(SERVICES_IMAGES_FOLDER);
            Path imagePath = SERVICES_IMAGES_FOLDER.resolve("service_" + id + ".jpg");
            image.transferTo(imagePath);
            service.setImageUrl("/admin/services/image/" + id);
        }

        serviceRepository.save(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/admin/services/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        serviceRepository.deleteById(id);
        return "redirect:/admin/services";
    }

    // --- IMÁGENES ---
    @GetMapping("/admin/classes/image/{id}")
    public ResponseEntity<Resource> getClassImage(@PathVariable Long id) throws IOException {
        return serveImage(CLASSES_IMAGES_FOLDER, "class_" + id + ".jpg");
    }

    @GetMapping("/admin/services/image/{id}")
    public ResponseEntity<Resource> getServiceImage(@PathVariable Long id) throws IOException {
        return serveImage(SERVICES_IMAGES_FOLDER, "service_" + id + ".jpg");
    }

    @GetMapping("/admin/members/image/{id}")
    public ResponseEntity<Resource> getMemberImage(@PathVariable Long id) throws IOException {
        return serveImage(USERS_IMAGES_FOLDER, "user_" + id + ".jpg");
    }

    private ResponseEntity<Resource> serveImage(Path folder, String fileName) throws IOException {
        Path imagePath = folder.resolve(fileName);
        Resource resource = new UrlResource(imagePath.toUri());
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
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

    @GetMapping("/admin/services/{id}/miembros")
    public String viewServiceMembers(@PathVariable Long id, Model model) {
        ServiceEntity service = serviceRepository.findById(id).orElse(null);
        if (service == null)
            return "redirect:/admin/services";

        List<User> todos = userRepository.findAll();
        List<User> inscritos = todos.stream()
                .filter(u -> u.getEnrolledServices().contains(service))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("service", service);
        model.addAttribute("inscritos", inscritos);
        model.addAttribute("totalInscritos", inscritos.size());
        return "admin-servicio-listado";
    }

    @GetMapping("/admin/services/new")
    public String showAddServiceForm() {
        return "admin-service-create";
    }

    @PostMapping("/admin/services/new")
    public String addService(@RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam("imageFile") MultipartFile image) throws IOException {

        ServiceEntity newService = new ServiceEntity();
        newService.setName(name);
        newService.setDescription(description);
        newService.setPrice(price);
        serviceRepository.save(newService);

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(SERVICES_IMAGES_FOLDER);
            Path imagePath = SERVICES_IMAGES_FOLDER.resolve("service_" + newService.getId() + ".jpg");
            image.transferTo(imagePath);
            newService.setImageUrl("/admin/services/image/" + newService.getId());
        } else {
            newService.setImageUrl("/img/service.jpg");
        }

        serviceRepository.save(newService);
        return "redirect:/admin/services";
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