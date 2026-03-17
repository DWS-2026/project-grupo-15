package es.codeurjc.proyecto_dws_grupo2.model; 

import jakarta.persistence.*;
import java.util.List;       // Añadido
import java.util.ArrayList;  // Añadido

@Entity
@Table(name = "users")
public class User { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private boolean extraFisio;
    private boolean extraNutricion;
    private boolean extraBebidas;

   
    @ManyToMany
    private List<ClassEntity> clasesApuntadas = new ArrayList<>();
  
    public User() { 
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isExtraFisio() {
        return extraFisio;
    }

    public void setExtraFisio(boolean extraFisio) {
        this.extraFisio = extraFisio;
    }

    public boolean isExtraNutricion() {
        return extraNutricion;
    }

    public void setExtraNutricion(boolean extraNutricion) {
        this.extraNutricion = extraNutricion;
    }

    public boolean isExtraBebidas() {
        return extraBebidas;
    }

    public void setExtraBebidas(boolean extraBebidas) {
        this.extraBebidas = extraBebidas;
    }
    
    public List<ClassEntity> getClasesApuntadas() {
        return clasesApuntadas;
    }

    public void setClasesApuntadas(List<ClassEntity > clasesApuntadas) {
        this.clasesApuntadas = clasesApuntadas;
    }
}