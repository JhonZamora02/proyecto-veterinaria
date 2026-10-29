/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.FormaPago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormaPagoDAO {

    private final MySQLConnection db = new MySQLConnection();

    // Listar todas las formas de pago activas
    public List<FormaPago> listarActivas() {
        List<FormaPago> lista = new ArrayList<>();
        String sql = "SELECT fp_id, fp_descripcion, fp_estado FROM forma_pago WHERE fp_estado='Activo'";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FormaPago e = new FormaPago();
                e.setId(rs.getInt("fp_id"));
                e.setDescripcion(rs.getString("fp_descripcion"));
                e.setEstado(rs.getString("fp_estado"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("Error listar forma de pago activas: " + ex.getMessage());
        }
        return lista;
    }

    // Listar todas las formas de pago (activas e inactivas)
    public List<FormaPago> listarTodas() {  // 👈 cambiado de listarTodos() a listarTodas()
        List<FormaPago> lista = new ArrayList<>();
        String sql = "SELECT fp_id, fp_descripcion, fp_estado FROM forma_pago";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FormaPago e = new FormaPago();
                e.setId(rs.getInt("fp_id"));
                e.setDescripcion(rs.getString("fp_descripcion"));
                e.setEstado(rs.getString("fp_estado"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("Error listar todas las formas de pago: " + ex.getMessage());
        }
        return lista;
    }

    // Insertar nueva especie
    public boolean agregar(FormaPago e) {
        String sql = "INSERT INTO forma_pago (fp_id, fp_descripcion, fp_estado) VALUES (?, ?, ?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, e.getId());
            ps.setString(2, e.getDescripcion());
            ps.setString(3, e.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error agregar Forma de pago: " + ex.getMessage());
            return false;
        }
    }

    // Actualizar forma de pago
    public boolean actualizar(FormaPago e) {
        String sql = "UPDATE forma_pago SET fp_descripcion=?, fp_estado=? WHERE fp_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getDescripcion());
            ps.setString(2, e.getEstado());
            ps.setInt(3, e.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error actualizar especie: " + ex.getMessage());
            return false;
        }
    }

    // Cambiar estado (activar/inactivar)
    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE forma_pago SET fp_estado=? WHERE fp_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error cambiar estado forma de pago: " + ex.getMessage());
            return false;
        }
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(fp_id) FROM forma_pago";
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
