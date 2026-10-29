/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Date;

public class Parametro {

    String para_id;
    String para_descripcion;
    String para_unidad;
    String para_estado;
    String usu_crea;
    Date fecha_crea;
    String para_usu_anu = "No Asignado";
    Date para_fecha_anu;
    String para_formula;
    String para_referencia1;
    String para_referencia2;

    public Parametro() {

    }

    public Parametro(String para_id, String para_descripcion, String para_unidad, String para_formula, String para_referencia1, String para_referencia2) {
        this.para_id = para_id;
        this.para_descripcion = para_descripcion;
        this.para_unidad = para_unidad;
        this.para_estado = "Activo"; // valor por defecto
        this.para_formula = para_formula;
        this.para_referencia1 = para_referencia1;
        this.para_referencia2 = para_referencia2;
    }

    public Parametro(String para_id, String para_descripcion, String para_unidad, String para_estado, String para_formula, String para_referencia1, String para_referencia2) {
        this.para_id = para_id;
        this.para_descripcion = para_descripcion;
        this.para_unidad = para_unidad;
        this.para_estado = para_estado;
        this.para_formula = para_formula;
        this.para_referencia1 = para_referencia1;
        this.para_referencia2 = para_referencia2;
    }

    public Parametro(String descripcion, String unidad, String formula, String referencia1, String referencia2) {
        this.para_descripcion = para_descripcion;
        this.para_unidad = para_unidad;
        this.para_formula = para_formula;
        this.para_referencia1 = para_referencia1;
        this.para_referencia2 = para_referencia2;

    }

    public String getPara_id() {
        return para_id;
    }

    public void setPara_id(String para_id) {
        this.para_id = para_id;
    }

    public String getPara_descripcion() {
        return para_descripcion;
    }

    public void setPara_descripcion(String para_descripcion) {
        this.para_descripcion = para_descripcion;
    }

    public String getPara_unidad() {
        return para_unidad;
    }

    public void setPara_unidad(String para_unidad) {
        this.para_unidad = para_unidad;
    }

    public String getPara_estado() {
        return para_estado;
    }

    public void setPara_estado(String para_estado) {
        this.para_estado = para_estado;
    }

    public String getUsu_crea() {
        return usu_crea;
    }

    public void setUsu_crea(String usu_crea) {
        this.usu_crea = usu_crea;
    }

    public String getPara_usu_anu() {
        return para_usu_anu;
    }

    public void setPara_usu_anu(String para_usu_anu) {
        this.para_usu_anu = para_usu_anu;
    }

    public String getPara_formula() {
        return para_formula;
    }

    public void setPara_formula(String para_formula) {
        this.para_formula = para_formula;
    }

    public String getPara_referencia1() {
        return para_referencia1;
    }

    public void setPara_referencia1(String para_referencia1) {
        this.para_referencia1 = para_referencia1;
    }

    public String getPara_referencia2() {
        return para_referencia2;
    }

    public void setPara_referencia2(String para_referencia2) {
        this.para_referencia2 = para_referencia2;
    }

    public Date getFecha_crea() {
        return fecha_crea;
    }

    public void setFecha_crea(Date fecha_crea) {
        this.fecha_crea = fecha_crea;
    }

    public Date getPara_fecha_anu() {
        return para_fecha_anu;
    }

    public void setPara_fecha_anu(Date para_fecha_anu) {
        this.para_fecha_anu = para_fecha_anu;
    }

}
