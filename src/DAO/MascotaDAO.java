/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Mascota;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAO {

    private final MySQLConnection db = new MySQLConnection();

    // ======================== MÉTODOS AUXILIARES ========================
    // Verificar si ya existe una mascota con el mismo ID
    public boolean existeMascota(int id) {
        String sql = "SELECT 1 FROM mascota WHERE mas_id = ?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error verificar existencia de mascota: " + e.getMessage());
            return true;
        }
    }

    // ======================== CRUD ========================
    // Crear nueva mascota
    public boolean agregar(Mascota m) {
        String sql = "INSERT INTO mascota (mas_id, mas_nombre, mas_edad, mas_propietario, mas_sexo, raza_id, mas_estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getEdad());
            ps.setString(4, m.getPropietario());
            ps.setString(5, m.getSexo());
            ps.setInt(6, m.getRazaId());
            ps.setString(7, m.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error agregar mascota: " + e.getMessage());
            return false;
        }
    }

    // Listar todas las mascotas
    public List<Mascota> listarTodas() {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT m.mas_id, m.mas_nombre, m.mas_edad, m.mas_propietario, "
                + "m.mas_sexo, r.raza_descripcion, m.mas_estado "
                + "FROM mascota m "
                + "INNER JOIN raza r ON m.raza_id = r.raza_id";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mascota m = new Mascota();
                m.setId(rs.getInt("mas_id"));
                m.setNombre(rs.getString("mas_nombre"));
                m.setEdad(rs.getInt("mas_edad"));
                m.setPropietario(rs.getString("mas_propietario"));
                m.setSexo(rs.getString("mas_sexo"));
                m.setRazaDescripcion(rs.getString("raza_descripcion"));
                m.setEstado(rs.getString("mas_estado"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error listar mascotas: " + e.getMessage());
        }
        return lista;
    }

    // Buscar mascotas por filtro (nombre, propietario, sexo, raza, estado o edad)
    public List<Mascota> buscar(String filtro) {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT m.mas_id, m.mas_nombre, m.mas_edad, m.mas_propietario, "
                + "m.mas_sexo, r.raza_descripcion, m.mas_estado "
                + "FROM mascota m "
                + "INNER JOIN raza r ON m.raza_id = r.raza_id "
                + "WHERE m.mas_nombre LIKE ? OR m.mas_propietario LIKE ? "
                + "OR m.mas_sexo LIKE ? OR r.raza_descripcion LIKE ? OR m.mas_estado LIKE ? "
                + "OR CAST(m.mas_edad AS CHAR) LIKE ?";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            String filtroLike = "%" + filtro + "%";
            for (int i = 1; i <= 6; i++) {
                ps.setString(i, filtroLike);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mascota m = new Mascota();
                    m.setId(rs.getInt("mas_id"));
                    m.setNombre(rs.getString("mas_nombre"));
                    m.setEdad(rs.getInt("mas_edad"));
                    m.setPropietario(rs.getString("mas_propietario"));
                    m.setSexo(rs.getString("mas_sexo"));
                    m.setRazaDescripcion(rs.getString("raza_descripcion"));
                    m.setEstado(rs.getString("mas_estado"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscar mascotas: " + e.getMessage());
        }
        return lista;
    }

    // Actualizar mascota
    public boolean actualizar(Mascota m) {
        String sql = "UPDATE mascota SET mas_nombre=?, mas_edad=?, mas_propietario=?, mas_sexo=?, raza_id=?, mas_estado=? WHERE mas_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getNombre());
            ps.setInt(2, m.getEdad());
            ps.setString(3, m.getPropietario());
            ps.setString(4, m.getSexo());
            ps.setInt(5, m.getRazaId());
            ps.setString(6, m.getEstado());
            ps.setInt(7, m.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizar mascota: " + e.getMessage());
            return false;
        }
    }

    // Cambiar estado (ej: Activo/Inactivo/Fallecido)
    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE mascota SET mas_estado=? WHERE mas_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiar estado mascota: " + e.getMessage());
            return false;
        }
    }

    // Eliminar controlado (realmente marca como Inactivo)
    public boolean eliminar(int id) {
        return cambiarEstado(id, "Inactivo");
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(mas_id) FROM mascota";
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
