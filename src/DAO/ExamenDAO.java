/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Examen;
import java.sql.*;
import java.util.*;

public class ExamenDAO {

    private final MySQLConnection db = new MySQLConnection();

    // Validar descripción única
    public boolean existeDescripcion(String descripcion) {
        String sql = "SELECT COUNT(*) FROM examen WHERE exa_descripcion=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Error validando descripción: " + ex.getMessage());
        }
        return false;
    }

    // Crear examen
    public boolean agregar(Examen e) {
        if (existeDescripcion(e.getDescripcion())) {
            System.err.println("La descripción ya existe.");
            return false;
        }
        String sql = "INSERT INTO examen (exa_id, exa_descripcion, exa_valor, exa_tipo, exa_estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.setString(2, e.getDescripcion());
            ps.setInt(3, e.getValor());
            ps.setString(4, e.getTipo());
            ps.setString(5, e.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error agregar examen: " + ex.getMessage());
            return false;
        }
    }

    // Listar
    public List<Examen> listar() {
        List<Examen> lista = new ArrayList<>();
        String sql = "SELECT * FROM examen";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Examen e = new Examen();
                e.setId(rs.getInt("exa_id"));
                e.setDescripcion(rs.getString("exa_descripcion"));
                e.setValor(rs.getInt("exa_valor"));
                e.setTipo(rs.getString("exa_tipo"));
                e.setEstado(rs.getString("exa_estado"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Error listar examenes: " + ex.getMessage());
        }
        return lista;
    }

    // Actualizar
    public boolean actualizar(Examen e) {
        String sql = "UPDATE examen SET exa_descripcion=?, exa_valor=?, exa_tipo=?, exa_estado=? WHERE exa_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getDescripcion());
            ps.setInt(2, e.getValor());
            ps.setString(3, e.getTipo());
            ps.setString(4, e.getEstado());
            ps.setInt(5, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error actualizar examen: " + ex.getMessage());
            return false;
        }
    }

    // Verificar si examen está en uso antes de anular
    private boolean estaEnUso(int id) {
        String sql1 = "SELECT COUNT(*) FROM examen_parametros WHERE exa_id=?";
        String sql2 = "SELECT COUNT(*) FROM orden_servicio_detalle WHERE exa_id=?";
        try (Connection con = db.getConexion()) {
            try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
                ps1.setInt(1, id);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next() && rs1.getInt(1) > 0) {
                    return true;
                }
            }
            try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
                ps2.setInt(1, id);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next() && rs2.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error verificando uso de examen: " + ex.getMessage());
        }
        return false;
    }

    // Cambiar estado
    public boolean cambiarEstado(int id, String nuevoEstado) {
        if (nuevoEstado.equalsIgnoreCase("Anulado") && estaEnUso(id)) {
            System.err.println("No se puede anular, examen en uso.");
            return false;
        }
        String sql = "UPDATE examen SET exa_estado=? WHERE exa_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error cambiar estado examen: " + ex.getMessage());
            return false;
        }
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(exa_id) FROM examen";
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
