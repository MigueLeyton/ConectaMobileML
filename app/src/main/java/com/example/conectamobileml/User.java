package com.example.conectamobileml;

public class User {
    private String userId;
    private String nombre;
    private String apellido;
    private String email;

    // Constructor público sin parámetros
    public User() {}

    // Constructor con id
    public User(String userId, String nombre, String apellido, String email) {
        this.userId = userId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Getters y setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
