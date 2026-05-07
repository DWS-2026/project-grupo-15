package es.codeurjc.proyecto_dws_grupo2.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.codeurjc.proyecto_dws_grupo2.model.ClassEntity;
import es.codeurjc.proyecto_dws_grupo2.model.Review;
import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.ClassRepository;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    public ClassService(ClassRepository classRepository, UserRepository userRepository) {
        this.classRepository = classRepository;
        this.userRepository = userRepository;
    }

    public List<ClassEntity> findAll() {
        return classRepository.findAll();
    }

    public Page<ClassEntity> findAll(Pageable pageable) {
        return classRepository.findAll(pageable);
    }

    public Optional<ClassEntity> findById(Long id) {
        return classRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return classRepository.existsById(id);
    }

    public ClassEntity save(ClassEntity classEntity) {
        if (classEntity.getDescription() != null) {
            String safeDescription = Jsoup.clean(classEntity.getDescription(), Safelist.relaxed());
            classEntity.setDescription(safeDescription);
        }
        return classRepository.save(classEntity);
    }

    public void deleteById(Long id) {
        classRepository.deleteById(id);
    }


    public List<User> getAttendees(Long classId) {
        return classRepository.findById(classId)
                .map(ClassEntity::getAttendees)
                .map(attendees -> new ArrayList<>(attendees))
                .orElse(new ArrayList<>());
    }


    public List<Review> getReviews(Long classId) {
        return classRepository.findById(classId)
                .map(ClassEntity::getReviews)
                .orElse(new ArrayList<>());
    }

    public void enrollUser(Long classId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow();
        
        user.getEnrolledClasses().add(classEntity);
        userRepository.save(user);
    }

    public void unenrollUser(Long classId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow();
        
        user.getEnrolledClasses().remove(classEntity);
        userRepository.save(user);
    }

    public boolean isUserEnrolled(Long classId, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        ClassEntity classEntity = classRepository.findById(classId).orElse(null);
        
        if (user != null && classEntity != null) {
            return user.getEnrolledClasses().contains(classEntity);
        }
        return false;
    }
}