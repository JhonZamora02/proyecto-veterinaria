/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Parametro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParametroDAO {

    // Insertar un nuevo parametro
    public boolean insertarParametro(Parametro p) {
        String sql = "INSERT INTO parametros (para_id, para_descripcion, para_unidad, para_estado, usu_crea, fecha_crea, para_usu_anu, para_formula, para_referencia1, para_referencia2) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getPara_id());
            ps.setString(2, p.getPara_descripcion());
            ps.setString(3, p.getPara_unidad());
            ps.setString(4, "Activo"); // valor por defecto al insertar
            ps.setString(5, p.getUsu_crea() == null ? "" : p.getUsu_crea());
            ps.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            // la columna para_usu_anu NO permite null según tu BD -> guardar cadena vacía si viene null
            ps.setString(7, p.getPara_usu_anu() == null ? "" : p.getPara_usu_anu());
            ps.setString(8, p.getPara_formula());
            ps.setString(9, p.getPara_referencia1());
            ps.setString(10, p.getPara_referencia2());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar parametro: " + e.getMessage());
            return false;
        }
    }

    // Modificar parametro (sin tocar estado/fechas)
    public boolean actualizar(Parametro p) {
        String sql = "UPDATE parametros SET para_descripcion=?, para_unidad=?, para_formula=?, para_referencia1=?, para_referencia2=? WHERE para_id=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getPara_descripcion());
            ps.setString(2, p.getPara_unidad());
            ps.setString(3, p.getPara_formula());
            ps.setString(4, p.getPara_referencia1());
            ps.setString(5, p.getPara_referencia2());
            ps.setString(6, p.getPara_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar parametro: " + e.getMessage());
            return false;
        }
    }

    // Alterna Estado: Activo <-> Anulado
    // - Si estaba Activo: pasa a Anulado y registra fecha+usuario de anulación.
    // - Si estaba Anulado: pasa a Activo y limpia para_usu_anu (cadena vacía). NO modifica para_fecha_anu.
    public boolean cambiarEstadoParametro(String id, String usuario) {
        String estadoActual = null;
        String sqlSelect = "SELECT para_estado FROM parametros WHERE para_id=?";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sqlSelect)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadoActual = rs.getString("para_estado");
                } else {
                    return false; // no existe
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al consultar estado actual: " + ex.getMessage());
            return false;
        }

        if ("Activo".equalsIgnoreCase(estadoActual)) {
            // Anular: set fecha actual y usuario
            String sqlUpdate = "UPDATE parametros SET para_estado='Anulado', para_fecha_anu=?, para_usu_anu=? WHERE para_id=?";
            try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sqlUpdate)) {

                ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(2, usuario == null ? "" : usuario);
                ps.setString(3, id);

                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                System.err.println("Error al anular parametro: " + ex.getMessage());
                return false;
            }
        } else {
            // Reactivar: solo poner estado Activo y limpiar para_usu_anu (no tocar para_fecha_anu)
            String sqlUpdate = "UPDATE parametros SET para_estado='Activo', para_usu_anu=? WHERE para_id=?";
            try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sqlUpdate)) {

                ps.setString(1, ""); // cadena vacía para columna NOT NULL
                ps.setString(2, id);

                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                System.err.println("Error al reactivar parametro: " + ex.getMessage());
                return false;
            }
        }
    }

    // Listar todos los parametros (para JTable)
    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT para_id, para_descripcion, para_unidad, para_estado, para_formula, para_referencia1, para_referencia2 FROM parametros";
        try (Connection con = MySQLConnection.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("para_id"),
                    rs.getString("para_descripcion"),
                    rs.getString("para_unidad"),
                    rs.getString("para_estado"),
                    rs.getString("para_formula"),
                    rs.getString("para_referencia1"),
                    rs.getString("para_referencia2")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar parametros: " + e.getMessage());
        }
        return lista;
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(para_id) FROM parametros";
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
