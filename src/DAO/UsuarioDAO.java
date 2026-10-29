/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final MySQLConnection db = new MySQLConnection();

    // Listar todas los usuarios
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.usu_id, u.usu_identificacion, u.usu_login, u.usu_pass, u.usu_nombre, u.usu_apellido, u.usu_email, u.usu_dir, u.usu_tel, u.usu_estado, p.per_descripcion FROM usuario u INNER JOIN perfil p ON u.per_id = p.per_id";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario objUsuario = new Usuario();
                objUsuario.setUsu_id(rs.getString("usu_id"));
                objUsuario.setUsu_identificacion(rs.getString("usu_identificacion"));
                objUsuario.setUsu_login(rs.getString("usu_login"));
                objUsuario.setUsu_pass(rs.getString("usu_pass"));
                objUsuario.setUsu_nombre(rs.getString("usu_nombre"));
                objUsuario.setUsu_apellido(rs.getString("usu_apellido"));
                objUsuario.setUsu_email(rs.getString("usu_email"));
                objUsuario.setUsu_dir(rs.getString("usu_dir"));
                objUsuario.setUsu_tel(rs.getString("usu_tel"));
                objUsuario.setUsu_estado(rs.getString("usu_estado"));
                objUsuario.setPer_descripcion(rs.getString("per_descripcion"));
                lista.add(objUsuario);
            }
        } catch (SQLException e) {
            System.err.println("Error listar usuarios: " + e.getMessage());
        }
        return lista;
    }
    
    // Crear nuevo Usuario
    public boolean agregar(Usuario Usu) {
        String sql = "INSERT INTO usuario (usu_id, usu_identificacion, usu_login, usu_pass, usu_nombre, usu_apellido, usu_email, usu_dir, usu_tel, usu_estado, per_id, img_firma) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, Usu.getUsu_id());
            ps.setString(2, Usu.getUsu_identificacion());
            ps.setString(3, Usu.getUsu_login());
            ps.setString(4, Usu.getUsu_pass());
            ps.setString(5, Usu.getUsu_nombre());
            ps.setString(6, Usu.getUsu_apellido());
            ps.setString(7, Usu.getUsu_email());
            ps.setString(8, Usu.getUsu_dir());
            ps.setString(9, Usu.getUsu_tel());
            ps.setString(10, Usu.getUsu_estado());
            ps.setString(11, Usu.getPer_id());
            ps.setString(12, Usu.getImg_firma());
            ps.execute();

            return true;
        } catch (SQLException e) {
            System.err.println("Error agregar usuario: " + e.getMessage());
            return false;
        }
    }

    // Actualizar los usuarios
    public boolean actualizar(Usuario Usu) {
        String sql = "UPDATE usuario SET usu_identificacion=?, usu_login=?, usu_pass=?, usu_nombre=?, "
                + "usu_apellido=?, usu_email=?, usu_dir=?, usu_tel=?, usu_estado=?, per_id=? "
                + "WHERE usu_id=?";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, Usu.getUsu_identificacion());
            ps.setString(2, Usu.getUsu_login());
            ps.setString(3, Usu.getUsu_pass());
            ps.setString(4, Usu.getUsu_nombre());
            ps.setString(5, Usu.getUsu_apellido());
            ps.setString(6, Usu.getUsu_email());
            ps.setString(7, Usu.getUsu_dir());
            ps.setString(8, Usu.getUsu_tel());
            ps.setString(9, Usu.getUsu_estado());
            ps.setString(10, Usu.getPer_id());
            ps.setString(11, Usu.getUsu_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Cambiar estado (activar / inactivar)
    public boolean cambiarEstado(String id, String nuevoEstado) {
        String sql = "UPDATE usuario SET usu_estado=? WHERE usu_id=?";
        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiar estado usuario: " + e.getMessage());
            return false;
        }
    }

    //Obtener el nuevo ID
    public int obtenerSiguienteId() {
        int siguienteId = 1; // Valor por defecto si no hay registros
        String sql = "SELECT MAX(usu_id) FROM usuario";
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
