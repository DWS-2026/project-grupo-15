package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.proyecto_dws_grupo2.dto.ServiceRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ServiceResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Servicios", description = "Gestión del catálogo de servicios y suplementos del gimnasio")
public class ServiceRestController {

    private final ServiceService serviceService;

    public ServiceRestController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ==========================================
    // 1. GET: OBTAIN ALL SERVICES (PAGINATED)
    // ==========================================
    @Operation(summary = "Obtener todos los servicios", description = "Devuelve una página con los servicios disponibles (Fisioterapia, Nutrición, Bebidas, etc.).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catálogo de servicios recuperado con éxito")
    })
    @GetMapping("/")
    public Page<ServiceResponseDTO> getServices(Pageable pageable) {
        return serviceService.findAllPaginated(pageable).map(ServiceResponseDTO::new);
    }

    // ==========================================
    // 2. GET: OBTAIN A SERVICE BY ID
    // ==========================================
    @Operation(summary = "Obtener un servicio por su ID", description = "Busca la información detallada de un servicio específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe ningún servicio con ese ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getService(@PathVariable Long id) {
        return serviceService.findById(id)
                .map(service -> ResponseEntity.ok(new ServiceResponseDTO(service)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 3. POST: CREATE A NEW SERVICE
    // ==========================================
    @Operation(summary = "Crear un nuevo servicio", description = "Añade un nuevo servicio al catálogo general.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Los datos del servicio son incorrectos", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<ServiceResponseDTO> createService(@RequestBody ServiceRequestDTO requestDTO) {
        ServiceEntity service = requestDTO.toEntity();
        ServiceEntity savedService = serviceService.save(service);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedService.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ServiceResponseDTO(savedService));
    }

    // ==========================================
    // 4. PUT: UPDATE AN EXISTING SERVICE
    // ==========================================
    @Operation(summary = "Actualizar un servicio", description = "Modifica los datos de un servicio ya existente en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado para actualizar", content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable Long id, 
            @RequestBody ServiceRequestDTO requestDTO) {
            
        Optional<ServiceEntity> serviceOpt = serviceService.findById(id);
        
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ServiceEntity existingService = serviceOpt.get();
        
        // Update the existing service entity with the new data from the request DTO
        requestDTO.updateEntity(existingService);
        
        ServiceEntity updatedService = serviceService.save(existingService);
        
        return ResponseEntity.ok(new ServiceResponseDTO(updatedService));
    }

    // ==========================================
    // 5. DELETE: DELETE A SERVICE BY ID
    // ==========================================
    @Operation(summary = "Eliminar un servicio", description = "Borra permanentemente un servicio del catálogo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado para eliminar", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> deleteService(@PathVariable Long id) {
        Optional<ServiceEntity> serviceOpt = serviceService.findById(id);
        
        if (serviceOpt.isPresent()) {
            serviceService.deleteById(id);
             return ResponseEntity.ok(new ServiceResponseDTO(serviceOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}