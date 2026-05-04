package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.dto.ClassDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/classes")
public class ClassRestController {

    private final ClassService classService;

    public ClassRestController(ClassService classService) {
        this.classService = classService;
    }

    // GET: Get all classes (with pagination)
    @GetMapping
    public ResponseEntity<List<ClassDTO>> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassDTO> classPage = classService.findAll(pageable)
                .map(ClassDTO::new);
        
        return ResponseEntity.ok(classPage.getContent());
    }

    // GET: Get class by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable Long id) {
        return classService.findById(id)
                .map(classEntity -> ResponseEntity.ok(new ClassDTO(classEntity)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create a new class
    @PostMapping
    public ResponseEntity<ClassDTO> createClass(@RequestBody ClassDTO classDTO) {
        ClassEntity classEntity = new ClassEntity(
            classDTO.name(),
            classDTO.description(),
            classDTO.schedule()
        );
        classEntity.setImageUrl(classDTO.imageUrl());
        
        ClassEntity saved = classService.save(classEntity);
        
        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(new ClassDTO(saved));
    }

    // PUT: Update a class completely
    @PutMapping("/{id}")
    public ResponseEntity<ClassDTO> updateClass(@PathVariable Long id, 
                                                 @RequestBody ClassDTO classDTO) {
        if (!classService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<ClassEntity> existingClass = classService.findById(id);
        if (existingClass.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ClassEntity classEntity = existingClass.get();
        classEntity.setName(classDTO.name());
        classEntity.setDescription(classDTO.description());
        classEntity.setSchedule(classDTO.schedule());
        classEntity.setImageUrl(classDTO.imageUrl());
        
        ClassEntity saved = classService.save(classEntity);
        
        return ResponseEntity.ok(new ClassDTO(saved));
    }

    // DELETE: Delete a class
    @DeleteMapping("/{id}")
    public ResponseEntity<ClassDTO> deleteClass(@PathVariable Long id) {
        Optional<ClassEntity> classEntity = classService.findById(id);
        
        if (classEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        classService.deleteById(id);
        return ResponseEntity.ok(new ClassDTO(classEntity.get()));
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