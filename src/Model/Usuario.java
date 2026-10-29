/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

public class Usuario {

    String usu_id;
    String usu_identificacion;
    String usu_login;
    String usu_pass;
    String usu_nombre;
    String usu_apellido;
    String usu_email;
    String usu_dir;
    String usu_tel;
    String usu_estado;
    String per_id;
    String img_firma = "img/firma.png";
    String per_descripcion;

    public Usuario() {

    }

    public Usuario(String usu_id, String usu_identificacion, String usu_login, String usu_pass, String usu_nombre, String usu_apellido, String usu_email, String usu_dir, String usu_tel, String usu_estado, String per_id, String img_firma) {
        this.usu_id = usu_id;
        this.usu_identificacion = usu_identificacion;
        this.usu_login = usu_login;
        this.usu_pass = usu_pass;
        this.usu_nombre = usu_nombre;
        this.usu_apellido = usu_apellido;
        this.usu_email = usu_email;
        this.usu_dir = usu_dir;
        this.usu_tel = usu_tel;
        this.usu_estado = usu_estado;
        this.per_id = per_id;
        this.img_firma = img_firma;
    }

    public String getUsu_id() {
        return usu_id;
    }

    public void setUsu_id(String usu_id) {
        this.usu_id = usu_id;
    }

    public String getUsu_identificacion() {
        return usu_identificacion;
    }

    public void setUsu_identificacion(String usu_identificacion) {
        this.usu_identificacion = usu_identificacion;
    }

    public String getUsu_login() {
        return usu_login;
    }

    public void setUsu_login(String usu_login) {
        this.usu_login = usu_login;
    }

    public String getUsu_pass() {
        return usu_pass;
    }

    public void setUsu_pass(String usu_pass) {
        this.usu_pass = usu_pass;
    }

    public String getUsu_nombre() {
        return usu_nombre;
    }

    public void setUsu_nombre(String usu_nombre) {
        this.usu_nombre = usu_nombre;
    }

    public String getUsu_apellido() {
        return usu_apellido;
    }

    public void setUsu_apellido(String usu_apellido) {
        this.usu_apellido = usu_apellido;
    }

    public String getUsu_email() {
        return usu_email;
    }

    public void setUsu_email(String usu_email) {
        this.usu_email = usu_email;
    }

    public String getUsu_dir() {
        return usu_dir;
    }

    public void setUsu_dir(String usu_dir) {
        this.usu_dir = usu_dir;
    }

    public String getUsu_tel() {
        return usu_tel;
    }

    public void setUsu_tel(String usu_tel) {
        this.usu_tel = usu_tel;
    }

    public String getUsu_estado() {
        return usu_estado;
    }

    public void setUsu_estado(String usu_estado) {
        this.usu_estado = usu_estado;
    }

    public String getPer_id() {
        return per_id;
    }

    public void setPer_id(String per_id) {
        this.per_id = per_id;
    }

    public String getImg_firma() {
        return img_firma;
    }

    public void setImg_firma(String img_firma) {
        this.img_firma = img_firma;
    }

    public String getPer_descripcion() {
        return per_descripcion;
    }

    public void setPer_descripcion(String per_descripcion) {
        this.per_descripcion = per_descripcion;
    }

}
