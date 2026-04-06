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

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
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
    private static final Path CLASSES_IMAGES_FOLDER = Paths.get("classes_images");
    private static final Path SERVICES_IMAGES_FOLDER = Paths.get("services_images");

    public AdminController(UserRepository userRepository, ClassRepository classRepository,
                       ServiceRepository serviceRepository, UserService userService,
                       ReviewRepository reviewRepository) { // <--- Añadir aquí
    this.userRepository = userRepository;
    this.classRepository = classRepository;
    this.serviceRepository = serviceRepository;
    this.userService = userService;
    this.reviewRepository = reviewRepository; // <--- Inicializar
}

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        long totalMembers = userRepository.count();
        model.addAttribute("totalMembers", totalMembers);

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
        model.addAttribute("totalClasses", 4);

        return "admin";
    }

    @GetMapping("/admin/members")
    public String adminMembers(Model model) {

        List<User> allMembers = userRepository.findAll();

        List<Map<String, Object>> members = new ArrayList<>();

        for (User u : allMembers) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", u.getId());

            String userPhoto = u.getProfileImageUrl();
            if (userPhoto == null || userPhoto.isEmpty()) {
                userPhoto = "/img/avatar.jpg";
            }

            map.put("photo", userPhoto);
            map.put("nombre", u.getFirstName() + " " + u.getLastName());
            map.put("email", u.getEmail());
            map.put("estado", "Activo");
            map.put("statusColor", "text-success");

            members.add(map);
        }

        model.addAttribute("members", members);

        return "admin-members";
    }

    @GetMapping("/admin/classes")
    public String adminClasses(Model model) {

        List<ClassEntity> allClasses = classRepository.findAll();
        List<Map<String, Object>> classes = new ArrayList<>();

        for (ClassEntity c : allClasses) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", c.getId());

            String classImage = c.getImageUrl();
            if (classImage == null || classImage.isEmpty()) {
                classImage = "/img/avatar.jpg";
            }
            map.put("imageUrl", classImage);
            map.put("name", c.getName());
            map.put("description", c.getDescription());
            map.put("schedule", c.getSchedule());

            classes.add(map);
        }

        model.addAttribute("classes", classes);

        return "admin-classes";
    }

    @PostMapping("/admin/classes/delete/{id}")
    public String deleteClass(@PathVariable Long id) {

        classRepository.deleteById(id);

        return "redirect:/admin/classes";
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
            String fileName = "class_" + newClass.getId() + ".jpg";
            Path imagePath = CLASSES_IMAGES_FOLDER.resolve(fileName);
            image.transferTo(imagePath);

            newClass.setImageUrl("/admin/classes/image/" + newClass.getId());
        } else {
            newClass.setImageUrl("/img/avatar.jpg");
        }

        classRepository.save(newClass);
        return "redirect:/admin/classes";
    }

    @GetMapping("/admin/classes/image/{id}")
    public ResponseEntity<Resource> getClassImage(@PathVariable Long id) throws IOException {
        Path imagePath = CLASSES_IMAGES_FOLDER.resolve("class_" + id + ".jpg");
        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/classes/edit/{id}")
    public String editClass(@PathVariable Long id, Model model) {
        ClassEntity clase = classRepository.findById(id).orElse(null);
        if (clase == null) {
            return "redirect:/login";
        }
        model.addAttribute("clase", clase);
        return "admin-class-edit";
    }

    @GetMapping("/admin/classes/{id}/asistentes")
    public String viewClassAttendees(@PathVariable Long id, Model model) {

        ClassEntity clase = classRepository.findById(id).orElse(null);
        if (clase == null)
            return "redirect:/admin/classes";

        List<User> todosLosUsuarios = userRepository.findAll();

        List<User> asistentes = new ArrayList<>();
        List<User> disponibles = new ArrayList<>();

        for (User u : todosLosUsuarios) {
            boolean estaApuntado = false;
            for (ClassEntity c : u.getEnrolledClasses()) {
                if (c.getId().equals(clase.getId())) {
                    estaApuntado = true;
                    break;
                }
            }

            if (estaApuntado) {
                asistentes.add(u);
            } else {

                if (!u.getEmail().equals("admin@titangym.com")) {
                    disponibles.add(u);
                }
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
            usuario.getEnrolledClasses().add(clase);
            userRepository.save(usuario); 
        }

        return "redirect:/admin/classes/" + id + "/asistentes";
    }

    @GetMapping("/admin/reviews")
    public String adminReviews(Model model) {
    List<Review> allReviews = reviewRepository.findAll();
    
    // Si la lista está vacía, enviamos una lista vacía, no un null
    model.addAttribute("reviews", allReviews != null ? allReviews : new ArrayList<>());
    
    return "admin-reviews";
}

    @PostMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/admin/profile")
    public String adminProfile(Model model) {
        return "admin-settings";
    }

    @GetMapping("/admin/services")
    public String adminServices(Model model) {

        List<ServiceEntity> allServices = serviceRepository.findAll();
        List<Map<String, Object>> entity = new ArrayList<>();

        for (ServiceEntity s : allServices) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", s.getId());

            String serviceImage = s.getImageUrl();
            if (serviceImage == null || serviceImage.isEmpty()) {
                serviceImage = "/img/avatar.jpg";
            }

            map.put("imageUrl", serviceImage);
            map.put("name", s.getName());
            map.put("description", s.getDescription());

            entity.add(map);
        }

        model.addAttribute("services", entity);

        return "admin-services";
    }

    @PostMapping("/admin/services/delete/{id}")
    public String deleteService(@PathVariable Long id) {

        serviceRepository.deleteById(id);

        return "redirect:/admin/services";
    }

    @GetMapping("/admin/services/edit/{id}")
    public String editService(@PathVariable Long id, Model model) {

        ServiceEntity service = serviceRepository.findById(id).orElse(null);
        if (service == null) {
            return "redirect:/admin/services";
        }
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

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(SERVICES_IMAGES_FOLDER);
            Path imagePath = SERVICES_IMAGES_FOLDER.resolve("service_" + id + ".jpg");
            image.transferTo(imagePath);
            service.setImageUrl("/admin/services/image/" + id);
        }

        serviceRepository.save(service);
        return "redirect:/admin/services";
    }

    @GetMapping("/admin/services/image/{id}")
    public ResponseEntity<Resource> getServiceImage(@PathVariable Long id) throws IOException {
        Path imagePath = SERVICES_IMAGES_FOLDER.resolve("service_" + id + ".jpg");
        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin/members/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        // Usamos el service en lugar del repository directamente 
        // para que se ejecute la lógica @Transactional y el borrado en cascada
        userService.deleteUser(id);

        return "redirect:/admin/members";

    }

    @GetMapping("/admin/members/{id}")
    public String viewMemberDetail(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
    
        if (user == null) {
            return "redirect:/admin/members";
        }

        model.addAttribute("user", user);
        // Añadimos las listas por separado para facilitar el bucle en Mustache
        model.addAttribute("classes", user.getEnrolledClasses());
        model.addAttribute("services", user.getEnrolledServices());
        model.addAttribute("reviews", user.getReviews());
    
    return "admin-usuario-detalle";
    }



    // MÉTODO 1: Mostrar el formulario con los datos actuales
    @GetMapping("/admin/members/edit/{id}")
        public String showEditUserForm(@PathVariable Long id, Model model) {
            User user = userRepository.findById(id).orElseThrow();
            model.addAttribute("user", user);
        return "admin-usuario-edit"; // El nombre del HTML que creamos arriba
    }

    @PostMapping("/admin/members/edit/{id}")
        public String updateUser(@PathVariable Long id, 
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String email,
                            @RequestParam(required = false) String newPassword,
                            @RequestParam(defaultValue = "false") boolean extraPhysio,
                            @RequestParam(defaultValue = "false") boolean extraNutrition,
                            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        User user = userRepository.findById(id).orElseThrow();
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setExtraPhysio(extraPhysio);
        user.setExtraNutrition(extraNutrition);

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword); // Recuerda encriptarla si usas Security
        }

        if (!imageFile.isEmpty()) {
            // Tu lógica de guardado de imagen aquí
        }

        userRepository.save(user);
        return "redirect:/admin/members"; // Redirige a la lista general
    }
       
}
