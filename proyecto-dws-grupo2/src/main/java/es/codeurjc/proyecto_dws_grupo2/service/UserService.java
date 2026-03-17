package es.codeurjc.proyecto_dws_grupo2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }
}