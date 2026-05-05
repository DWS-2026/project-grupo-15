package es.codeurjc.proyecto_dws_grupo2.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.proyecto_dws_grupo2.dto.ServiceDTO;
import es.codeurjc.proyecto_dws_grupo2.dto.ServiceMapper;
import es.codeurjc.proyecto_dws_grupo2.service.ServiceService;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper; // <-- 1. Añadimos el Mapper

    // 2. Lo inyectamos en el constructor
    public ServiceRestController(ServiceService serviceService, ServiceMapper serviceMapper) {
        this.serviceService = serviceService;
        this.serviceMapper = serviceMapper;
    }

    @GetMapping("/")
    public Page<ServiceDTO> getServices(Pageable pageable) {
        // 3. Usamos el Mapper en lugar de ServiceDTO::new
        return serviceService.findAllPaginated(pageable).map(serviceMapper::toDTO);
    }
}