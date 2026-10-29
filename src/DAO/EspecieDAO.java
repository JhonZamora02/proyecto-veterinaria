/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Especie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecieDAO {

    private final MySQLConnection db = new MySQLConnection();

    // ======================== LISTAR ========================
    public List<Especie> listarActivas() {
        List<Especie> lista = new ArrayList<>();
        String sql = "SELECT esp_id, esp_descripcion, esp_estado FROM especie WHERE esp_estado='Activo'";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Especie e = new Especie();
                e.setId(rs.getInt("esp_id"));
                e.setDescripcion(rs.getString("esp_descripcion"));
                e.setEstado(rs.getString("esp_estado"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Error listar especies activas: " + ex.getMessage());
        }
        return lista;
    }

    public List<Especie> listarTodas() {
        List<Especie> lista = new ArrayList<>();
        String sql = "SELECT esp_id, esp_descripcion, esp_estado FROM especie";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Especie e = new Especie();
                e.setId(rs.getInt("esp_id"));
                e.setDescripcion(rs.getString("esp_descripcion"));
                e.setEstado(rs.getString("esp_estado"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Error listar todas las especies: " + ex.getMessage());
        }
        return lista;
    }

    // ======================== BUSCAR ========================
    public List<Especie> buscarPorDescripcion(String desc) {
        List<Especie> lista = new ArrayList<>();
        String sql = "SELECT esp_id, esp_descripcion, esp_estado FROM especie WHERE esp_descripcion LIKE ?";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + desc + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Especie e = new Especie();
                    e.setId(rs.getInt("esp_id"));
                    e.setDescripcion(rs.getString("esp_descripcion"));
                    e.setEstado(rs.getString("esp_estado"));
                    lista.add(e);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error buscar especie: " + ex.getMessage());
        }
        return lista;
    }

    // ======================== CRUD ========================
    public boolean agregar(Especie e) {
        String sql = "INSERT INTO especie (esp_id, esp_descripcion, esp_estado) VALUES (?, ?, ?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, e.getId());
            ps.setString(2, e.getDescripcion());
            ps.setString(3, e.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error agregar especie: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizar(Especie e) {
        String sql = "UPDATE especie SET esp_descripcion=?, esp_estado=? WHERE esp_id=?";
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

    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE especie SET esp_estado=? WHERE esp_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error cambiar estado especie: " + ex.getMessage());
            return false;
        }
    }

    // ======================== ELIMINAR CONTROLADO ========================
    public boolean eliminar(int id) {
        String checkRaza = "SELECT 1 FROM raza WHERE esp_id = ? LIMIT 1";
        String checkMascota = "SELECT 1 FROM mascota m INNER JOIN raza r ON m.raza_id = r.raza_id WHERE r.esp_id = ? LIMIT 1";
        boolean ok = false;

        try (Connection con = db.getConexion()) {
            boolean tieneDependencias = false;

            // Verificar dependencias en raza
            try (PreparedStatement ps1 = con.prepareStatement(checkRaza)) {
                ps1.setInt(1, id);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        tieneDependencias = true;
                    }
                }
            }

            // Verificar dependencias en mascota
            try (PreparedStatement ps2 = con.prepareStatement(checkMascota)) {
                ps2.setInt(1, id);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        tieneDependencias = true;
                    }
                }
            }

            if (tieneDependencias) {
                // Si hay dependencias -> marcar Inactivo
                String sqlUpdate = "UPDATE especie SET esp_estado='Inactivo' WHERE esp_id=?";
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, id);
                    ok = ps.executeUpdate() > 0;
                }
            } else {
                // Si no hay dependencias -> eliminar físicamente
                String sqlDelete = "DELETE FROM especie WHERE esp_id=?";
                try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
                    ps.setInt(1, id);
                    ok = ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error eliminar especie: " + ex.getMessage());
        }
        return ok;
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(esp_id) FROM especie";
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
