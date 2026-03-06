package es.codeurjc.proyecto_dws_grupo2.config;

import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRegistration.Dynamic;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2ConsoleConfig implements ServletContextInitializer {

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext) {
        ServletRegistration.Dynamic registration =
                servletContext.addServlet("h2-console", new JakartaWebServlet());

        registration.setLoadOnStartup(1);
        registration.addMapping("/h2-console/*");
    }
}
