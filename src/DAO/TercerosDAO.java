/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Terceros;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TercerosDAO {

    // Insertar un nuevo tercero
    public boolean agregar(Terceros t) {
        String sql = "INSERT INTO terceros (id, ti_descripcion, identificacion, representante_legal, razon_social, direccion, tel1, tel2, observaciones, estado, usu_crea, fecha_crea, usu_anula, fecha_anula, razon_anula) "
                + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), '', NOW(), '')";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getId());
            ps.setString(2, t.getDescripcion());
            ps.setString(3, t.getIdentificacion());
            ps.setString(4, t.getRepresentantelegal());
            ps.setString(5, t.getRazonsocial());
            ps.setString(6, t.getDireccion());
            ps.setString(7, t.getTelefono1());
            ps.setString(8, t.getTelefono2());
            ps.setString(9, t.getObservaciones());
            ps.setString(10, "Activo");
            ps.setString(11, t.getUsucrea());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error agregar terceros: " + e.getMessage());
            return false;
        }
    }

    // Validar si una identificacion ya existe
    public boolean existeIdentificacion(String identificacion) {
        String sql = "SELECT COUNT(*) FROM terceros WHERE identificacion=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar identificacion: " + e.getMessage());
        }
        return false;
    }

    // Buscar tercero por identificacion
    public Terceros buscaridentificacion(String identificacion) {
        String sql = "SELECT * FROM terceros WHERE identificacion=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Terceros t = new Terceros(
                        rs.getInt("id"),
                        rs.getString("ti_descripcion"),
                        rs.getString("identificacion"),
                        rs.getString("representante_legal"),
                        rs.getString("razon_social"),
                        rs.getString("direccion"),
                        rs.getString("tel1"),
                        rs.getString("tel2"),
                        rs.getString("observaciones"),
                        rs.getString("estado")
                );
                t.setUsucrea(rs.getString("usu_crea"));
                t.setFechacrea(rs.getString("fecha_crea"));
                t.setUsuanula(rs.getString("usu_anula"));
                t.setFechanula(rs.getString("fecha_anula"));
                t.setRazonanula(rs.getString("razon_anula"));
                return t;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Terceros: " + e.getMessage());
        }
        return null;
    }

    // Modificar Tercero
    public boolean modificarTercero(Terceros t) {
        String sql = "UPDATE terceros SET  ti_descripcion=?, identificacion=?, representante_legal=?, razon_social=?, direccion=?, tel1=?, tel2=?, observaciones=? WHERE id=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, t.getDescripcion());
            ps.setString(2, t.getIdentificacion());
            ps.setString(3, t.getRepresentantelegal());
            ps.setString(4, t.getRazonsocial());
            ps.setString(5, t.getDireccion());
            ps.setString(6, t.getTelefono1());
            ps.setString(7, t.getTelefono2());
            ps.setString(8, t.getObservaciones());
            ps.setInt(9, t.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar Terceros: " + e.getMessage());
            return false;
        }
    }

    // Cambiar estado (Activo <-> Anulado)
    public boolean cambiarEstado(String identificacion, String usuarioLogueado, boolean anular) {
        String sql;
        if (anular) {

            sql = "UPDATE terceros SET estado='Anulado', usu_anula=?, fecha_anula=NOW() WHERE identificacion=?";
        } else {

            sql = "UPDATE terceros SET estado='Activo', usu_anula='', fecha_anula=NOW(), razon_anula='' WHERE identificacion=?";
        }

        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (anular) {
                ps.setString(1, usuarioLogueado);
                ps.setString(2, identificacion);
            } else {
                ps.setString(1, identificacion);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado del tercero: " + e.getMessage());
            return false;
        }
    }

    // Listar todos los terceros
    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id, ti_descripcion, identificacion, representante_legal, razon_social, direccion, tel1, tel2, observaciones, estado FROM terceros";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ti_descripcion"),
                    rs.getString("identificacion"),
                    rs.getString("representante_legal"),
                    rs.getString("razon_social"),
                    rs.getString("direccion"),
                    rs.getString("tel1"),
                    rs.getString("tel2"),
                    rs.getString("observaciones"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar terceros: " + e.getMessage());
        }
        return lista;
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(id) FROM terceros";
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
