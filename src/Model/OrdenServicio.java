/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;
import java.util.List;

public class OrdenServicio {

    // ==============================
    // CAMPOS BASE (tabla orden_servicio)
    // ==============================
    private int id;
    private Date fecha;
    private int idVeterinario;
    private int idMascota;
    private String propietario;
    private String estado;
    private double total;
    private double abono;
    private double saldo;
    private String observaciones;
    private String usuarioCreador;

    // ==============================
    // CAMPOS DE APOYO (para listados y vista)
    // ==============================
    private String veterinario;
    private String veterinaria;
    private String mascota;

    private String nombreVeterinario;
    private String nombreVeterinaria;
    private String nombreMascota;

    private int idExamenFiltro;
    private String nombreExamen;

    // Lista de detalles asociados a la orden
    private List<OrdenServicioDetalle> detalles;

    // ==============================
    // CONSTRUCTORES
    // ==============================
    public OrdenServicio() {
    }

    public OrdenServicio(int id, Date fecha, int idVeterinario, int idMascota, String propietario,
            String estado, double total, double abono, double saldo,
            String observaciones, String usuarioCreador) {
        this.id = id;
        this.fecha = fecha;
        this.idVeterinario = idVeterinario;
        this.idMascota = idMascota;
        this.propietario = propietario;
        this.estado = estado;
        this.total = total;
        this.abono = abono;
        this.saldo = saldo;
        this.observaciones = observaciones;
        this.usuarioCreador = usuarioCreador;
    }

    // ==============================
    // GETTERS Y SETTERS
    // ==============================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public List<OrdenServicioDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenServicioDetalle> detalles) {
        this.detalles = detalles;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(String veterinaria) {
        this.veterinaria = veterinaria;
    }

    public String getMascota() {
        return mascota;
    }

    public void setMascota(String mascota) {
        this.mascota = mascota;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getNombreVeterinaria() {
        return nombreVeterinaria;
    }

    public void setNombreVeterinaria(String nombreVeterinaria) {
        this.nombreVeterinaria = nombreVeterinaria;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public int getIdExamenFiltro() {
        return idExamenFiltro;
    }

    public void setIdExamenFiltro(int idExamenFiltro) {
        this.idExamenFiltro = idExamenFiltro;
    }

    public String getNombreExamen() {
        return nombreExamen;
    }

    public void setNombreExamen(String nombreExamen) {
        this.nombreExamen = nombreExamen;
    }
}
