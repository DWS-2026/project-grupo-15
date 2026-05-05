package es.codeurjc.proyecto_dws_grupo2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;

@Service 
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // 1. Web method to get all services (not paginated, for internal use or admin panel)
    public List<ServiceEntity> findAll() {
        return serviceRepository.findAll();
    }

    // 2. Web method to get paginated services 
    public Page<ServiceEntity> findAllPaginated(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    // 3. Web method to get a service by its ID
    public Optional<ServiceEntity> findById(Long id) {
        return serviceRepository.findById(id);
    }

    // 4. Web method to save a new service or update an existing one
    public ServiceEntity save(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    // 5. Web method to delete a service by its ID
    public void deleteById(Long id) {
        serviceRepository.deleteById(id);
    }
}