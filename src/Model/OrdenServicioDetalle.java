/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class OrdenServicioDetalle {

    private int id;
    private int idOrden;
    private int idExamen;
    private double valor;
    private String cantidad;
    private String observaciones;
    private double subtotal;
    private String descripcionExamen;

    // Constructor vacío
    public OrdenServicioDetalle() {
    }

    // Constructor con parámetros
    public OrdenServicioDetalle(int id, int idOrden, int idExamen, double valor,
            String cantidad, String observaciones, double subtotal) {
        this.id = id;
        this.idOrden = idOrden;
        this.idExamen = idExamen;
        this.valor = valor;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.subtotal = subtotal;
    }

    // ====================== GETTERS & SETTERS ======================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getDescripcionExamen() {
        return descripcionExamen;
    }

    public void setDescripcionExamen(String descripcionExamen) {
        this.descripcionExamen = descripcionExamen;
    }
}
