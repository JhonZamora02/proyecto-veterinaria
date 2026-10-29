/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Veterinario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {

    // Insertar un nuevo veterinario
    public boolean insertarVeterinario(Veterinario v) {
        String sql = "INSERT INTO veterinario (vet_id, vet_nombre, vet_email, vet_tel, vet_veterinaria, vet_direccion, vet_estado, usu_crea, fecha_crea, usu_anula, fecha_anula) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getId());
            ps.setString(2, v.getNombre());
            ps.setString(3, v.getEmail());
            ps.setString(4, v.getTelefono());
            ps.setString(5, v.getVeterinaria());
            ps.setString(6, v.getDireccion());
            ps.setString(7, "Activo"); // valor por defecto
            ps.setString(8, v.getUsu_crea());
            ps.setDate(9, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(10, v.getUsu_anula());
            ps.setString(11, "1900-01-01");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar veterinario: " + e.getMessage());
            return false;
        }
    }

    // Validar si un email ya existe
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM veterinario WHERE vet_email=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar email: " + e.getMessage());
        }
        return false;
    }

    // Buscar veterinario por email
    public Veterinario buscarPorEmail(String email) {
        String sql = "SELECT * FROM veterinario WHERE vet_email=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Veterinario(
                        rs.getInt("vet_id"),
                        rs.getString("vet_nombre"),
                        rs.getString("vet_email"),
                        rs.getString("vet_tel"),
                        rs.getString("vet_veterinaria"),
                        rs.getString("vet_direccion"),
                        rs.getString("vet_estado")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar veterinario: " + e.getMessage());
        }
        return null;
    }

    // Modificar veterinario (por email como identificador único)
    public boolean modificarVeterinario(Veterinario v) {
        String sql = "UPDATE veterinario SET vet_id=?, vet_nombre=?, vet_tel=?, vet_veterinaria=?, vet_direccion=? WHERE vet_email=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getId());
            ps.setString(2, v.getNombre());
            ps.setString(3, v.getTelefono());
            ps.setString(4, v.getVeterinaria());
            ps.setString(5, v.getDireccion());
            ps.setString(6, v.getEmail());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar veterinario: " + e.getMessage());
            return false;
        }
    }

    // Anular veterinario
    public boolean anularVeterinario(String email, String usu_anula) {
        String sql = "UPDATE veterinario SET vet_estado='Anulado', fecha_anula=?, usu_anula=? WHERE vet_email=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(2, usu_anula);
            ps.setString(3, email);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al anular veterinario: " + e.getMessage());
            return false;
        }
    }

    // Listar todos los veterinarios
    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT vet_id, vet_nombre, vet_email, vet_tel, vet_veterinaria, vet_direccion, vet_estado FROM veterinario";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("vet_id"),
                    rs.getString("vet_nombre"),
                    rs.getString("vet_email"),
                    rs.getString("vet_tel"),
                    rs.getString("vet_veterinaria"),
                    rs.getString("vet_direccion"),
                    rs.getString("vet_estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar veterinarios: " + e.getMessage());
        }
        return lista;
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(vet_id) FROM veterinario";
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
