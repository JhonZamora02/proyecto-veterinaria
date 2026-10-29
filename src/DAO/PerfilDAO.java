/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Perfil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO extends MySQLConnection {

    public List<Perfil> listarActivas() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Perfil> lista = new ArrayList<>();
        String sql = "SELECT per_id, per_descripcion, per_estado FROM `perfil`";
        MySQLConnection con = new MySQLConnection();
        con.getConexion();

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Perfil objPerfil = new Perfil();
                objPerfil.setPer_id(rs.getString("per_id"));
                objPerfil.setPer_descripcion(rs.getString("per_descripcion"));
                objPerfil.setPer_estado(rs.getString("per_estado"));
                lista.add(objPerfil);
            }

        } catch (SQLException e) {
            System.err.println("Error listar perfiles: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT per_id, per_descripcion, per_estado FROM `perfil`";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("per_id"),
                    rs.getString("per_descripcion"),
                    rs.getString("per_estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar perfiles: " + e.getMessage());
        }
        return lista;
    }

    // Insertar nueva perfil
    public boolean agregar(Perfil p) {
        String sql = "INSERT INTO perfil (per_id, per_descripcion, per_estado) VALUES (?, ?, ?)";
        MySQLConnection con = new MySQLConnection();
        con.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getPer_id());
            ps.setString(2, p.getPer_descripcion());
            ps.setString(3, p.getPer_estado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error agregar raza: " + e.getMessage());
            return false;
        }
    }

    // Actualizar perfil
    public boolean actualizar(Perfil p) {
        String sql = "UPDATE perfil SET per_descripcion=?, per_estado=? WHERE per_id=?";
        MySQLConnection con = new MySQLConnection();
        con.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getPer_descripcion());
            ps.setString(2, p.getPer_estado());
            ps.setString(3, p.getPer_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar raza: " + e.getMessage());
            return false;
        }
    }

    // Cambiar estado (activar/anular)
    public boolean cambiarEstado(String id, String nuevoEstado) {
        String sql = "UPDATE perfil SET per_estado=? WHERE per_id=?";
        MySQLConnection con = new MySQLConnection();
        con.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiar estado de raza: " + e.getMessage());
            return false;
        }
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(per_id) FROM perfil";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                siguienteId = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el siguiente ID: " + e.getMessage());
        }
        return siguienteId;
    }

}
