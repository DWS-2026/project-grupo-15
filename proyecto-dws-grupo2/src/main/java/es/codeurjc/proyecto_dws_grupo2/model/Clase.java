package es.codeurjc.proyecto_dws_grupo2.model;

public class Clase {
    private int id;
    private String name;
    private String description;

    // Constructor
    public Clase(int id, String name, String description) {
        this.id = id; //Keep the id
        this.name = name;
        this.description = description;
    }

    // Getters (THIS IS IMPORTANT: Mustache needs this to read the data)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}