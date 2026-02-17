package es.codeurjc.proyecto_dws_grupo2.model; 

import jakarta.persistence.*;

@Entity
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

   
    public User() { 
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

    // ... añade los getters/setters de los extras si faltan
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
}