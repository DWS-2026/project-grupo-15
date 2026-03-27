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
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final ServiceRepository serviceRepository;
    private static final Path CLASSES_IMAGES_FOLDER = Paths.get("classes_images");

    public AdminController(UserRepository userRepository, ClassRepository classRepository, ServiceRepository serviceRepository) {
        this.userRepository = userRepository;
        this.classRepository = classRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/admin")
    public String adminPanel(HttpSession session, Model model) {
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
        model.addAttribute("totalTrainers", 5);
        model.addAttribute("monthlyGrowth", "+18%");

        return "admin";
    }

    @GetMapping("/admin/members")
    public String adminMembers(HttpSession session, Model model) {

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
    public String adminClasses(HttpSession session, Model model) {

        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || !usuarioLogado.getEmail().equals("admin@titangym.com")) {
            return "redirect:/login"; 
        }
        
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

    //BORRAR CLASE 
    @PostMapping("/admin/classes/delete/{id}")
    public String deleteClass(@PathVariable Long id, HttpSession session) {
        
        //  Comprobar si es admin
        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || !usuarioLogado.getEmail().equals("admin@titangym.com")) {
            return "redirect:/login";
        }

        // Borramos de la base de datos y recargamos la página de la lista
        classRepository.deleteById(id);
        
        return "redirect:/admin/classes";
    }

    @GetMapping("/admin/classes/new")
    public String showCreateForm(HttpSession session) {
        
        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || !usuarioLogado.getEmail().equals("admin@titangym.com")) {
            return "redirect:/login";
        }

        // Carga tu archivo HTML del formulario
        return "admin-clase-crear"; 
    }

    // --- 4. ATRAPAR DATOS Y GUARDAR NUEVA CLASE ---
    @PostMapping("/admin/classes/new")
    public String saveNewClass(
            @RequestParam String name,
            @RequestParam String schedule,
            @RequestParam String description,
            @RequestParam("imageField") org.springframework.web.multipart.MultipartFile imageField,
            HttpSession session) throws java.io.IOException {
        
        // Portero: Comprobamos si es la admin
        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || !usuarioLogado.getEmail().equals("admin@titangym.com")) {
            return "redirect:/login";
        }

        // 1. Lógica para guardar la imagen subida
        String imageUrl = "/img/avatar.jpg"; // Imagen por defecto por si falla
        
        if (!imageField.isEmpty()) {
            // Sacamos el nombre original de la foto (ej: zumba.jpg)
            String fileName = imageField.getOriginalFilename();
            
            // Le decimos a Java dónde guardar el archivo en tu disco duro
            // (Se guardará en la carpeta static/img de tu proyecto)
            java.nio.file.Path ruta = java.nio.file.Paths.get("src/main/resources/static/img", fileName);
            java.nio.file.Files.write(ruta, imageField.getBytes());
            
            // La ruta que guardaremos en la base de datos para que el HTML la encuentre
            imageUrl = "/img/" + fileName;
        }

        // 2. Creamos la clase con los datos del formulario
        ClassEntity nuevaClase = new ClassEntity(name, description, schedule);
        
        // 3. La guardamos en la base de datos
        classRepository.save(nuevaClase);

        // 4. Volvemos al listado de clases donde ya debería aparecer
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