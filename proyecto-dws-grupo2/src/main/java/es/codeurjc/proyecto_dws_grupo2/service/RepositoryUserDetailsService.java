package es.codeurjc.proyecto_dws_grupo2.service;

import es.codeurjc.proyecto_dws_grupo2.model.User;
import es.codeurjc.proyecto_dws_grupo2.repository.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final GlobalLoginAttemptService loginAttemptService;

    public RepositoryUserDetailsService(UserRepository userRepository,
                                        GlobalLoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // BLOCK CHECK: Must happen before DB lookup. Spring will fire
        // AuthenticationFailureLockedEvent (not BadCredentials), so the
        // failure counter will NOT increment again for a locked user.
        if (loginAttemptService.isBlocked(email)) {
            throw new LockedException("Account temporarily locked due to too many failed attempts.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // FIX: Replaced manual ArrayList loop with a stream for clarity
        List<GrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                roles
        );
    }
}