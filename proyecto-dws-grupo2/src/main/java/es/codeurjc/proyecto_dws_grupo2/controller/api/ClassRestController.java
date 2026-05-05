package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.dto.ClassResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ClassRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/classes")
public class ClassRestController {

    private final ClassService classService;

    public ClassRestController(ClassService classService) {
        this.classService = classService;
    }

    // GET: Get all classes (with pagination)
    @GetMapping
    public ResponseEntity<Page<ClassResponseDTO>> getAllClasses(Pageable pageable) {
        Page<ClassResponseDTO> classPage = classService.findAll(pageable)
                .map(ClassResponseDTO::new);

        return ResponseEntity.ok(classPage);
    }

    // GET: Get class by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> getClassById(@PathVariable Long id) {
        return classService.findById(id)
                .map(classEntity -> ResponseEntity.ok(new ClassResponseDTO(classEntity)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create a new class
   @PostMapping
    public ResponseEntity<ClassResponseDTO> createClass(@RequestBody ClassRequestDTO classRequestDTO) {
        
        ClassEntity classEntity = classRequestDTO.toEntity();
        
        ClassEntity saved = classService.save(classEntity);
        
        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(new ClassResponseDTO(saved));
    }

    // PUT: Update a class completely
    @PutMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> updateClass(@PathVariable Long id, 
                                                 @RequestBody ClassRequestDTO classRequestDTO) {
        
        Optional<ClassEntity> existingClassOpt = classService.findById(id);
        
        if (existingClassOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ClassEntity classEntity = existingClassOpt.get();
        
        classRequestDTO.updateEntity(classEntity);
        
        ClassEntity saved = classService.save(classEntity);
        
        return ResponseEntity.ok(new ClassResponseDTO(saved));
    }

    // DELETE: Delete a class
    @DeleteMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> deleteClass(@PathVariable Long id) {
        Optional<ClassEntity> classEntity = classService.findById(id);

        if (classEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        classService.deleteById(id);
        return ResponseEntity.ok(new ClassResponseDTO(classEntity.get()));
    }

    // GET: Get all attendees of a class
    @GetMapping("/{id}/attendees")
    public ResponseEntity<?> getClassAttendees(@PathVariable Long id) {
        if (!classService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classService.getAttendees(id));
    }

    // GET: Get all reviews of a class
    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getClassReviews(@PathVariable Long id) {
        if (!classService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classService.getReviews(id));
    }
}

