/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.OrdenServicioDetalle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioDetalleDAO {

    // ======================================================
    // MÉTODO 1 - GUARDAR DETALLES DE UNA ORDEN
    // ======================================================
    public boolean guardarDetalles(List<OrdenServicioDetalle> detalles, int idOrden) {
        if (detalles == null || detalles.isEmpty() || idOrden <= 0) {
            System.err.println("No hay detalles validos para guardar.");
            return false;
        }

        String sqlInsert = "INSERT INTO orden_servicio_detalle "
                + "(dos_id, dos_os_id, dos_exa_id, dos_exa_valor, dos_exa_cantidad, dos_exa_observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = MySQLConnection.getConexion()) {

            for (OrdenServicioDetalle det : detalles) {
                // Calcular ID único por cada examen
                int nuevoId = obtenerSiguienteId(con);

                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setInt(1, nuevoId);
                    ps.setInt(2, idOrden);
                    ps.setInt(3, det.getIdExamen());
                    ps.setDouble(4, det.getValor());
                    ps.setString(5, det.getCantidad() != null ? det.getCantidad() : "1");
                    ps.setString(6, det.getObservaciones() != null ? det.getObservaciones() : "");
                    ps.executeUpdate();
                }
            }

            System.out.println("Se guardaron " + detalles.size() + " detalles para la orden " + idOrden);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar detalles: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ======================================================
    // MÉTODO AUXILIAR - OBTENER SIGUIENTE ID DISPONIBLE
    // ======================================================
    private int obtenerSiguienteId(Connection con) throws SQLException {
        String sql = "SELECT IFNULL(MAX(dos_id), 0) + 1 AS nuevoId FROM orden_servicio_detalle";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nuevoId");
            }
        }
        return 1;
    }

    // ======================================================
    // MÉTODO 2 - LISTAR DETALLES DE UNA ORDEN
    // ======================================================
    public List<OrdenServicioDetalle> listarPorOrden(int idOrden) {
        List<OrdenServicioDetalle> lista = new ArrayList<>();

        String sql = "SELECT d.dos_id, d.dos_os_id, d.dos_exa_id, d.dos_exa_valor, "
                + "d.dos_exa_cantidad, d.dos_exa_observaciones, e.exa_descripcion "
                + "FROM orden_servicio_detalle d "
                + "INNER JOIN examen e ON e.exa_id = d.dos_exa_id "
                + "WHERE d.dos_os_id = ?";

        try (Connection con = MySQLConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrden);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdenServicioDetalle det = new OrdenServicioDetalle();
                    det.setId(rs.getInt("dos_id"));
                    det.setIdOrden(rs.getInt("dos_os_id"));
                    det.setIdExamen(rs.getInt("dos_exa_id"));
                    det.setValor(rs.getDouble("dos_exa_valor"));
                    det.setCantidad(rs.getString("dos_exa_cantidad"));
                    det.setObservaciones(rs.getString("dos_exa_observaciones"));
                    det.setDescripcionExamen(rs.getString("exa_descripcion"));

                    // Calcular subtotal
                    double subtotal;
                    try {
                        subtotal = det.getValor() * Double.parseDouble(det.getCantidad());
                    } catch (NumberFormatException ex) {
                        subtotal = det.getValor();
                    }
                    det.setSubtotal(subtotal);

                    lista.add(det);
                }
            }

            System.out.println("Se encontraron " + lista.size() + " examenes para la orden " + idOrden);

        } catch (SQLException e) {
            System.err.println("Error al listar detalles por orden: " + e.getMessage());
        }

        return lista;
    }

    // ======================================================
    // MÉTODO 3 - ELIMINAR DETALLES POR ORDEN
    // ======================================================
    public boolean eliminarPorOrden(int idOrden) {
        String sql = "DELETE FROM orden_servicio_detalle WHERE dos_os_id = ?";
        try (Connection con = MySQLConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrden);
            int filas = ps.executeUpdate();
            System.out.println(" Se eliminaron " + filas + " detalles de la orden " + idOrden);
            return filas > 0;

        } catch (SQLException e) {
            System.err.println(" Error al eliminar detalles: " + e.getMessage());
            return false;
        }
    }
    
}
