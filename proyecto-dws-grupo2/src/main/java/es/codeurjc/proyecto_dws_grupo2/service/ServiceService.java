package es.codeurjc.proyecto_dws_grupo2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.proyecto_dws_grupo2.model.ServiceEntity;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ServiceRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service 
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceService(ServiceRepository serviceRepository, UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
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

    public ServiceEntity findByName(String name) {
        return serviceRepository.findByName(name);
    }

    // 4. Web method to save a new service or update an existing one
    public ServiceEntity save(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    // 5. Web method to delete a service by its ID
    public void deleteById(Long id) {
        serviceRepository.deleteById(id);
    }

    // 6. Web method to check if a service exists
    public boolean existsById(Long id) {
        return serviceRepository.existsById(id);
    }

    // 7. Web method to get all users enrolled in a service
    public List<User> getEnrolledUsers(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .map(ServiceEntity::getEnrolledUsers)
                .map(users -> new ArrayList<>(users))
                .orElse(new ArrayList<>());
    }

    public void enrollUser(Long serviceId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ServiceEntity service = serviceRepository.findById(serviceId).orElseThrow();
        user.getEnrolledServices().add(service);
        userRepository.save(user);
    }

    public void unsubscribeUser(Long serviceId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ServiceEntity service = serviceRepository.findById(serviceId).orElse(null);

        if (service != null) {
            user.getEnrolledServices().remove(service);
            userRepository.save(user);
        }
    }

    public void updateUserExtraServices(User user, boolean extraPhysio, boolean extraNutrition, boolean extraDrinks) {
        user.getEnrolledServices().clear();
        addExtraServiceIfSelected(user, extraPhysio, "Fisioterapia");
        addExtraServiceIfSelected(user, extraNutrition, "Nutrición");
        addExtraServiceIfSelected(user, extraDrinks, "Bebidas Extra");
    }

    public void addSelectedServices(User user, boolean extraPhysio, boolean extraNutrition, boolean extraDrinks) {
        addExtraServiceIfSelected(user, extraPhysio, "Fisioterapia");
        addExtraServiceIfSelected(user, extraNutrition, "Nutrición");
        addExtraServiceIfSelected(user, extraDrinks, "Bebidas Extra");
    }

    private void addExtraServiceIfSelected(User user, boolean selected, String serviceName) {
        if (selected) {
            ServiceEntity service = serviceRepository.findByName(serviceName);
            if (service != null) {
                user.getEnrolledServices().add(service);
            }
        }
    }
}
