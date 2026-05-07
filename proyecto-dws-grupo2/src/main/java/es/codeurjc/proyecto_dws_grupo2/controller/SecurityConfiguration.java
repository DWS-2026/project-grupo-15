package es.codeurjc.proyecto_dws_grupo2.controller;

import es.codeurjc.proyecto_dws_grupo2.jwt.JwtRequestFilter;
import es.codeurjc.proyecto_dws_grupo2.jwt.JwtTokenProvider;
import es.codeurjc.proyecto_dws_grupo2.jwt.UnauthorizedHandlerJwt;
import es.codeurjc.proyecto_dws_grupo2.service.GlobalLoginAttemptService;
import es.codeurjc.proyecto_dws_grupo2.service.RepositoryUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RepositoryUserDetailsService userDetailService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // Re-added: needed by the failure handler to compute remaining attempts
    @Autowired
    private GlobalLoginAttemptService loginAttemptService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.core.AuthenticationException exception) -> {

            if (exception instanceof LockedException) {
                // Account is already locked — show locked message
                response.sendRedirect("/loginerror?locked=true");
            } else {
                String email = request.getParameter("email");

                if (loginAttemptService.isBlocked(email)) {
                    response.sendRedirect("/loginerror?locked=true");
                } else {
                    int remaining = loginAttemptService.getRemainingAttempts(email);
                    response.sendRedirect("/loginerror?remaining=" + remaining);
                }
            }
        };
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/classes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/classes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/classes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/classes/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/services/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/services/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/services/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/users/*/image").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated());

        http.formLogin(formLogin -> formLogin.disable());
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/lib/**", "/mail/**")
                        .permitAll()
                        .requestMatchers("/", "/login", "/loginerror", "/register", "/error",
                                "/classes/info", "/classes", "/about", "/contact", "/feature",
                                "/payment_success")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .failureHandler(authenticationFailureHandler())
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                            response.sendRedirect(isAdmin ? "/admin" : "/profile");
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
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            if (token != null) {
                modelAndView.addObject("token", token.getToken());
            }
        }
    }
}