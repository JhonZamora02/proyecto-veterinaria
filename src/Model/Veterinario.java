/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Date;

public class Veterinario {

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String veterinaria;
    private String direccion;
    private String estado;
    private Date fecha_creacion;
    private String usu_crea;
    private Date fecha_anula;
    private String usu_anula = "No Asignado";

    // Constructor vacío
    public Veterinario() {
    }

    // Constructor usado para registrar un nuevo veterinario SIN estado explícito
    public Veterinario(int id, String nombre, String email, String telefono, String veterinaria, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.veterinaria = veterinaria;
        this.direccion = direccion;
        this.estado = "Activo"; // valor por defecto
    }

    public Veterinario(int id, String nombre, String email, String telefono, String veterinaria, String direccion, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.veterinaria = veterinaria;
        this.direccion = direccion;
        this.estado = estado;
    }

    // ================= Getters y Setters =================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(String veterinaria) {
        this.veterinaria = veterinaria;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getUsu_crea() {
        return usu_crea;
    }

    public void setUsu_crea(String usu_crea) {
        this.usu_crea = usu_crea;
    }

    public Date getFecha_anula() {
        return fecha_anula;
    }

    public void setFecha_anula(Date fecha_anula) {
        this.fecha_anula = fecha_anula;
    }

    public String getUsu_anula() {
        return usu_anula;
    }

    public void setUsu_anula(String usu_anula) {
        this.usu_anula = usu_anula;
    }

}
