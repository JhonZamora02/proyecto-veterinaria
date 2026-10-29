/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Login;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    public boolean validarCredenciales(String usuario, String contrasena) {

        // La consulta SQL debe seleccionar los campos 'usu_login' y 'usu_pass'
        String sql = "SELECT COUNT(*) FROM usuario WHERE usu_login = ? AND usu_pass = ? AND usu_estado = 'Activo'";

        // Uso de un try-with-resources para asegurar el cierre de la conexión y el statement
        try (Connection conn = MySQLConnection.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignar los valores a los parámetros de la consulta
            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);

            // Ejecutar la consulta y obtener el resultado
            ResultSet rs = pstmt.executeQuery();

            // Si el resultado tiene al menos una fila (es decir, el conteo es > 0), 
            // significa que las credenciales son correctas.
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {
            // Manejo de errores en caso de problemas con la base de datos
            System.err.println("Error de validación de credenciales: " + ex.getMessage());
        }
        return false;
    }

}
