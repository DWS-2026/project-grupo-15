package es.codeurjc.proyecto_dws_grupo2.controller.api;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.proyecto_dws_grupo2.dto.ClassResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ServiceRequestDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ServiceResponseDTO;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    private final ServiceService serviceService;

    // 1. Ya NO inyectamos ServiceMapper
    public ServiceRestController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ==========================================
    // 1. OBTENER TODOS LOS SERVICIOS (Paginado)
    // ==========================================
    @GetMapping("/")
    public Page<ServiceResponseDTO> getServices(Pageable pageable) {
        // Usamos el constructor del ServiceResponseDTO
        return serviceService.findAllPaginated(pageable).map(ServiceResponseDTO::new);
    }

    // ==========================================
    // 2. OBTENER UN SOLO SERVICIO POR SU ID
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        Optional<ServiceEntity> serviceOpt = serviceService.findById(id);
        
        if (serviceOpt.isPresent()) {
            // Usamos el constructor del ServiceResponseDTO
            return ResponseEntity.ok(new ServiceResponseDTO(serviceOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // 3. CREAR UN NUEVO SERVICIO
    // ==========================================
    @PostMapping("/")
    public ResponseEntity<ServiceResponseDTO> createService(@RequestBody ServiceRequestDTO requestDTO) {
        
        // 2. Usamos el método inteligente del DTO
        ServiceEntity newService = requestDTO.toEntity();
        
        ServiceEntity savedService = serviceService.save(newService);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedService.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(new ServiceResponseDTO(savedService));
    }

    // ==========================================
    // 4. ACTUALIZAR UN SERVICIO EXISTENTE
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable Long id, 
            @RequestBody ServiceRequestDTO requestDTO) {
            
        Optional<ServiceEntity> serviceOpt = serviceService.findById(id);
        
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ServiceEntity existingService = serviceOpt.get();
        
        // 3. Usamos el método inteligente del DTO para actualizar
        requestDTO.updateEntity(existingService);
        
        ServiceEntity updatedService = serviceService.save(existingService);
        
        return ResponseEntity.ok(new ServiceResponseDTO(updatedService));
    }

    // ==========================================
    // 5. BORRAR UN SERVICIO
    // ==========================================
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