package es.codeurjc.proyecto_dws_grupo2.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;

    public AdminController(UserRepository userRepository, ClassRepository classRepository) {
        this.userRepository = userRepository;
        this.classRepository = classRepository;
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

            classes.add(map);
        }

        model.addAttribute("classes", classes);
        
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