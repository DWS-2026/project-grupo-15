package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.proyecto_dws_grupo2.dto.ClassResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ClassRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.service.ClassService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

// --- Imports de Swagger / OpenAPI ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/classes")
@Tag(name = "Clases", description = "Gestión de las clases y actividades del gimnasio")
public class ClassRestController {

    private final ClassService classService;

    public ClassRestController(ClassService classService) {
        this.classService = classService;
    }

    // ==========================================
    // GET: Obtener todas las clases (Paginadas)
    // ==========================================
    @Operation(summary = "Obtener todas las clases", description = "Devuelve una página con todas las clases o actividades disponibles en el gimnasio.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clases recuperada con éxito")
    })
    @GetMapping
    public ResponseEntity<Page<ClassResponseDTO>> getAllClasses(Pageable pageable) {
        Page<ClassResponseDTO> classPage = classService.findAll(pageable)
                .map(ClassResponseDTO::new);

        return ResponseEntity.ok(classPage);
    }

    // ==========================================
    // GET: Obtener clase por ID
    // ==========================================
    @Operation(summary = "Obtener una clase por su ID", description = "Busca una clase específica utilizando su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clase encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "No existe ninguna clase con ese ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> getClassById(@PathVariable Long id) {
        return classService.findById(id)
                .map(classEntity -> ResponseEntity.ok(new ClassResponseDTO(classEntity)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // POST: Crear una nueva clase
    // ==========================================
    @Operation(summary = "Crear una nueva clase", description = "Añade una nueva clase o actividad al catálogo del gimnasio.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Clase creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Los datos proporcionados para la clase son inválidos", content = @Content)
    })
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

    // ==========================================
    // PUT: Actualizar una clase completamente
    // ==========================================
    @Operation(summary = "Actualizar una clase existente", description = "Modifica los datos completos de una clase ya registrada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clase actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada para actualizar", content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content)
    })
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

    // ==========================================
    // DELETE: Borrar una clase
    // ==========================================
    @Operation(summary = "Eliminar una clase", description = "Borra permanentemente una clase del sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clase eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada para eliminar", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> deleteClass(@PathVariable Long id) {
        Optional<ClassEntity> classEntity = classService.findById(id);

        if (classEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        classService.deleteById(id);
        // Ojo: devuelves un 200 con el objeto borrado, lo cual es válido y lo he reflejado en la documentación.
        return ResponseEntity.ok(new ClassResponseDTO(classEntity.get()));
    }

    // ==========================================
    // GET: Obtener asistentes de una clase
    // ==========================================
    @Operation(summary = "Obtener asistentes de una clase", description = "Recupera la lista de todos los usuarios inscritos en una clase específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de asistentes recuperada con éxito"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada", content = @Content)
    })
    @GetMapping("/{id}/attendees")
    public ResponseEntity<?> getClassAttendees(@PathVariable Long id) {
        if (!classService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classService.getAttendees(id));
    }

    // ==========================================
    // GET: Obtener reviews de una clase
    // ==========================================
    @Operation(summary = "Obtener reseñas de una clase", description = "Recupera todas las valoraciones y comentarios asociados a una clase concreta.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reseñas recuperada con éxito"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada", content = @Content)
    })
    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getClassReviews(@PathVariable Long id) {
        if (!classService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classService.getReviews(id));
    }
}

