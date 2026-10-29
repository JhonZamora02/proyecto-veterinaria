/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class Mascota {

    private int id;
    private String nombre;
    private int edad;
    private String propietario;
    private String sexo;
    private int razaId;
    private String estado;
    private String razaDescripcion;

    public Mascota() {
    }

    public Mascota(int id, String nombre, int edad, String propietario, String sexo, int razaId, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.propietario = propietario;
        this.sexo = sexo;
        this.razaId = razaId;
        this.estado = estado;
    }

    // ================= GETTERS & SETTERS =================
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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getRazaId() {
        return razaId;
    }

    public void setRazaId(int razaId) {
        this.razaId = razaId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRazaDescripcion() {
        return razaDescripcion;
    }

    public void setRazaDescripcion(String razaDescripcion) {
        this.razaDescripcion = razaDescripcion;
    }

}
