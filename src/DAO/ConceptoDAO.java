/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Concepto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConceptoDAO {

    private final MySQLConnection db = new MySQLConnection();

    // Listar todos los conceptos activas
    public List<Concepto> listarActivas() {
        List<Concepto> lista = new ArrayList<>();
        String sql = "SELECT con_id, con_descripcion, con_estado FROM concepto WHERE con_estado='Activo'";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Concepto e = new Concepto();
                e.setId(rs.getInt("con_id"));
                e.setDescripcion(rs.getString("con_descripcion"));
                e.setEstado(rs.getString("con_estado"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("Error listar concepto activas: " + ex.getMessage());
        }
        return lista;
    }

    // Listar todos los conceptos (activas e inactivas)
    public List<Concepto> listarTodas() {  // 👈 cambiado de listarTodos() a listarTodas()
        List<Concepto> lista = new ArrayList<>();
        String sql = "SELECT con_id, con_descripcion, con_estado FROM concepto";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Concepto e = new Concepto();
                e.setId(rs.getInt("con_id"));
                e.setDescripcion(rs.getString("con_descripcion"));
                e.setEstado(rs.getString("con_estado"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("Error listar todos los Conceptos: " + ex.getMessage());
        }
        return lista;
    }

    // Insertar nuevo concepto
    public boolean agregar(Concepto e) {
        String sql = "INSERT INTO concepto (con_id, con_descripcion, con_estado) VALUES (?, ?, ?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, e.getId());
            ps.setString(2, e.getDescripcion());
            ps.setString(3, e.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error agregar concepto: " + ex.getMessage());
            return false;
        }
    }

    // Actualizar concepto
    public boolean actualizar(Concepto e) {
        String sql = "UPDATE concepto SET con_descripcion=?, con_estado=? WHERE con_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getDescripcion());
            ps.setString(2, e.getEstado());
            ps.setInt(3, e.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error actualizar concepto: " + ex.getMessage());
            return false;
        }
    }

    // Cambiar estado (activar/inactivar)
    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE concepto SET con_estado=? WHERE con_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error cambiar estado concepto: " + ex.getMessage());
            return false;
        }
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(con_id) FROM concepto";
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
