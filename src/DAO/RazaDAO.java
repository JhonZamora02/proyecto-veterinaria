/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Raza;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RazaDAO {

    private final MySQLConnection db = new MySQLConnection();

    // ======================== LISTAR ========================
    public List<Raza> listarActivas() {
        List<Raza> lista = new ArrayList<>();
        String sql = "SELECT raza_id, raza_descripcion, raza_estado, esp_id FROM raza WHERE raza_estado='Activo'";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Raza r = new Raza();
                r.setId(rs.getInt("raza_id"));
                r.setDescripcion(rs.getString("raza_descripcion"));
                r.setEstado(rs.getString("raza_estado"));
                r.setEspecieId(rs.getInt("esp_id"));
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error listar razas activas: " + e.getMessage());
        }
        return lista;
    }

    // Listar todas las razas
    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT r.raza_id, r.raza_descripcion, r.raza_estado, e.esp_descripcion "
                + "FROM raza r INNER JOIN especie e ON r.esp_id = e.esp_id";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getInt("raza_id");
                fila[1] = rs.getString("raza_descripcion");
                fila[2] = rs.getString("raza_estado");
                fila[3] = rs.getString("esp_descripcion");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error listar todas las razas: " + e.getMessage());
        }
        return lista;
    }

    // ======================== AGREGAR ========================
    public boolean agregar(Raza r) {
        String sql = "INSERT INTO raza (raza_id, raza_descripcion, raza_estado, esp_id) VALUES (?, ?, ?, ?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());
            ps.setInt(4, r.getEspecieId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error agregar raza: " + e.getMessage());
            return false;
        }
    }

    // ======================== ACTUALIZAR ========================
    public boolean actualizar(Raza r) {
        String sql = "UPDATE raza SET raza_descripcion=?, raza_estado=?, esp_id=? WHERE raza_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getDescripcion());
            ps.setString(2, r.getEstado());
            ps.setInt(3, r.getEspecieId());
            ps.setInt(4, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar raza: " + e.getMessage());
            return false;
        }
    }

    // ======================== CAMBIAR ESTADO ========================
    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE raza SET raza_estado=? WHERE raza_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiar estado de raza: " + e.getMessage());
            return false;
        }
    }

    // ======================== ELIMINAR CONTROLADO ========================
    public boolean eliminar(int id) {
        // Validar dependencias en la tabla mascota
        String sqlValidar = "SELECT COUNT(*) FROM mascota WHERE raza_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sqlValidar)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Si tiene dependencias, marcar como Inactivo
                return cambiarEstado(id, "Inactivo");
            }
        } catch (SQLException e) {
            System.err.println("Error validando dependencias antes de eliminar raza: " + e.getMessage());
            return false;
        }

        // Si no tiene dependencias, eliminar físicamente
        String sqlDelete = "DELETE FROM raza WHERE raza_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sqlDelete)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar raza: " + e.getMessage());
            return false;
        }
    }

    // ======================== BUSCAR ========================
    public List<Object[]> buscarPorDescripcion(String descripcion) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT r.raza_id, r.raza_descripcion, r.raza_estado, e.esp_descripcion "
                + "FROM raza r INNER JOIN especie e ON r.esp_id = e.esp_id "
                + "WHERE r.raza_descripcion LIKE ?";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + descripcion + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getInt("raza_id");
                fila[1] = rs.getString("raza_descripcion");
                fila[2] = rs.getString("raza_estado");
                fila[3] = rs.getString("esp_descripcion");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error buscar raza: " + e.getMessage());
        }
        return lista;
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(raza_id) FROM raza";
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
