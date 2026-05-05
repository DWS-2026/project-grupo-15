package es.codeurjc.proyecto_dws_grupo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ProyectoDwsGrupo2Application {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoDwsGrupo2Application.class, args);
    }

}