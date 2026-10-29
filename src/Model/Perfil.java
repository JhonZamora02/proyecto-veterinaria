/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

public class Perfil {

    String per_id;
    String per_descripcion;
    String per_estado;

    public Perfil() {

    }

    public Perfil(String per_id, String per_descripcion, String per_estado) {
        this.per_id = per_id;
        this.per_descripcion = per_descripcion;
        this.per_estado = per_estado;
    }

    public String getPer_id() {
        return per_id;
    }

    public void setPer_id(String per_id) {
        this.per_id = per_id;
    }

    public String getPer_descripcion() {
        return per_descripcion;
    }

    public void setPer_descripcion(String per_descripcion) {
        this.per_descripcion = per_descripcion;
    }

    public String getPer_estado() {
        return per_estado;
    }

    public void setPer_estado(String per_estado) {
        this.per_estado = per_estado;
    }

    @Override
    public String toString() {
        return per_descripcion;
    }

}
