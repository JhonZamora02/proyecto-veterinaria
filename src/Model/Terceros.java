/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class Terceros {

    private int id;
    private String descripcion;
    private String identificacion;
    private String representantelegal;
    private String razonsocial;
    private String direccion;
    private String telefono1;
    private String telefono2;
    private String observaciones;
    private String usucrea;
    private String fechacrea;
    private String usuanula;
    private String fechanula;
    private String razonanula;
    private String estado;

    public Terceros() {
    }
    // Constructor usado para registrar un nuevo Tercero  SIN estado explícito

    public Terceros(int id, String descripcion, String identificacion, String representantelegal, String razonsocial, String direccion, String telefono1, String telefono2, String observaciones) {
        this.id = id;
        this.descripcion = descripcion;
        this.identificacion = identificacion;
        this.representantelegal = representantelegal;
        this.razonsocial = razonsocial;
        this.direccion = direccion;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.observaciones = observaciones;
        this.estado = "Activo";

    }
    // Constructor completo

    public Terceros(int id, String descripcion, String identificacion, String representantelegal, String razonsocial, String direccion, String telefono1, String telefono2, String observaciones, String estado) {
        this.id = id;
        this.descripcion = descripcion;
        this.identificacion = identificacion;
        this.representantelegal = representantelegal;
        this.razonsocial = razonsocial;
        this.direccion = direccion;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.observaciones = observaciones;
        this.estado = estado;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getRepresentantelegal() {
        return representantelegal;
    }

    public void setRepresentantelegal(String representantelegal) {
        this.representantelegal = representantelegal;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsucrea() {
        return usucrea;
    }

    public void setUsucrea(String usucrea) {
        this.usucrea = usucrea;
    }

    public String getFechacrea() {
        return fechacrea;
    }

    public void setFechacrea(String fechacrea) {
        this.fechacrea = fechacrea;
    }

    public String getUsuanula() {
        return usuanula;
    }

    public void setUsuanula(String usuanula) {
        this.usuanula = usuanula;
    }

    public String getFechanula() {
        return fechanula;
    }

    public void setFechanula(String fechanula) {
        this.fechanula = fechanula;
    }

    public String getRazonanula() {
        return razonanula;
    }

    public void setRazonanula(String razonanula) {
        this.razonanula = razonanula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
