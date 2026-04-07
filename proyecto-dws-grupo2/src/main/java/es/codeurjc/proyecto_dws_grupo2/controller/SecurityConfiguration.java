package es.codeurjc.proyecto_dws_grupo2.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Recursos estáticos siempre públicos
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/lib/**", "/mail/**").permitAll()
                        
                        // 2. Imágenes de Clases y Servicios: ¡DEBEN SER PÚBLICAS! 
                        // Aunque el controlador sea Admin, la ruta de visualización no debe estar bloqueada
                        .requestMatchers("/admin/classes/image/**", "/admin/services/image/**", "/profile/image/**").permitAll()

                        // 3. Páginas públicas (He añadido /classes para que se vean las actividades)
                        .requestMatchers("/", "/login", "/loginerror", "/register", "/error", 
                                        "/classes/info", "/classes", "/about", "/contact", "/feature", 
                                        "/payment_success").permitAll()

                        // 4. Panel de administración
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 5. Todo lo demás (perfil, reviews, apuntarse a clases, comprar extras) requiere login
                        .anyRequest().authenticated())

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email") 
                        .failureUrl("/loginerror")
                        .successHandler((request, response, authentication) -> {
                            // Verificamos si es admin para mandarlo a su panel
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                            
                            if (isAdmin) {
                                response.sendRedirect("/admin"); 
                            } else {
                                response.sendRedirect("/profile"); 
                            }
                        })
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CSRFHandlerInterceptor());
    }
}


class CSRFHandlerInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            if (token != null) {
                // Pasa el token al HTML con el nombre "token"
                modelAndView.addObject("token", token.getToken());
            }
        }
    }
}