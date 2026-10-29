/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnection {

    private static Connection con;
    public static final String puerto = "3306";
    public static final String nomservidor = "localhost";
    public static final String db = "labpets";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "root";
    private static final String pass = "";
    private static final String url = "jdbc:mysql://" + nomservidor + ":" + puerto + "/" + db;

    public static Connection getConexion() {
        con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                System.out.println("Conexion establecida..");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar " + e);
        }
        return con;
    }

    public void desconectar() {
        con = null;
        if (con == null) {
            System.out.println("Conexion terminada..");
        }
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            return con.prepareStatement(sql);
        } catch (Exception e) {
            System.err.println("Error preparing statement" + e.getMessage());
        }
        // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return null;
    }
}
