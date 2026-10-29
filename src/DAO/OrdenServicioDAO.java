/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.OrdenServicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioDAO {

    private final MySQLConnection db = new MySQLConnection();

    // ======================================================
    // GUARDAR ORDEN (estado por defecto: EN PROCESO)
    // ======================================================
    public int guardarOrden(OrdenServicio orden) {
        int idGenerado = 0;

        String sql = "INSERT INTO orden_servicio ("
                + "os_id, os_fecha, os_vet_id, os_mas_id, os_mas_propietario, "
                + "os_estado, os_valor_total, os_abono, os_saldo, "
                + "os_observaciones, os_observacionesResultados, os_usuResultado_crea) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orden.getId());
            ps.setDate(2, new java.sql.Date(orden.getFecha().getTime()));
            ps.setInt(3, orden.getIdVeterinario());
            ps.setInt(4, orden.getIdMascota());
            ps.setString(5, orden.getPropietario() != null ? orden.getPropietario() : "");

            // Estado: EN PROCESO
            ps.setString(6, "En proceso");

            ps.setDouble(7, orden.getTotal());
            ps.setDouble(8, orden.getAbono());
            ps.setDouble(9, orden.getSaldo());

            ps.setString(10, orden.getObservaciones() != null ? orden.getObservaciones() : "");
            ps.setString(11, "");
            ps.setString(12, orden.getUsuarioCreador() != null ? orden.getUsuarioCreador() : "");

            int filas = ps.executeUpdate();
            if (filas > 0) {
                idGenerado = orden.getId();
                System.out.println(" Orden registrada correctamente con ID " + idGenerado);
            }

        } catch (SQLException e) {
            System.err.println(" Error al guardar la orden de servicio: " + e.getMessage());
        }

        return idGenerado;
    }

    // ======================================================
    // LISTAR ÓRDENES SEGÚN LOS FILTROS
    // ======================================================
    public List<OrdenServicio> listarOrdenes(
            java.util.Date fechaInicio,
            java.util.Date fechaFin,
            String numeroOrden,
            String estado,
            String nombreVeterinario,
            String nombreMascota,
            String nombreExamen
    ) {

        List<OrdenServicio> lista = new ArrayList<>();

        boolean filtrandoExamen = (nombreExamen != null && !nombreExamen.isEmpty());

        StringBuilder sql = new StringBuilder();

        if (filtrandoExamen) {
            // ============================================================
            // MODO 1: Filtrar por examen → UNA FILA POR EXAMEN
            // ============================================================
            sql.append(
                    "SELECT "
                    + "os.os_id, "
                    + "os.os_fecha, "
                    + "v.vet_nombre AS veterinario, "
                    + "m.mas_nombre AS mascota, "
                    + "e.exa_descripcion AS examen, "
                    + "os.os_valor_total AS total, "
                    + "os.os_abono AS abono, "
                    + "os.os_saldo AS saldo, "
                    + "os.os_estado AS estado "
                    + "FROM orden_servicio_detalle d "
                    + "INNER JOIN orden_servicio os ON os.os_id = d.dos_os_id "
                    + "INNER JOIN veterinario v ON v.vet_id = os.os_vet_id "
                    + "INNER JOIN mascota m ON m.mas_id = os.os_mas_id "
                    + "INNER JOIN examen e ON e.exa_id = d.dos_exa_id "
                    + "WHERE os.os_fecha BETWEEN ? AND ? "
            );
        } else {
            // ============================================================
            // MODO 2: Búsqueda normal (sin examen) → UNA FILA POR ORDEN
            // ============================================================
            sql.append(
                    "SELECT "
                    + "os.os_id, "
                    + "os.os_fecha, "
                    + "v.vet_nombre AS veterinario, "
                    + "m.mas_nombre AS mascota, "
                    + "(SELECT GROUP_CONCAT(e2.exa_descripcion SEPARATOR ', ') "
                    + " FROM orden_servicio_detalle d2 "
                    + " INNER JOIN examen e2 ON e2.exa_id = d2.dos_exa_id "
                    + " WHERE d2.dos_os_id = os.os_id) AS examen, "
                    + "os.os_valor_total AS total, "
                    + "os.os_abono AS abono, "
                    + "os.os_saldo AS saldo, "
                    + "os.os_estado AS estado "
                    + "FROM orden_servicio os "
                    + "INNER JOIN veterinario v ON v.vet_id = os.os_vet_id "
                    + "INNER JOIN mascota m ON m.mas_id = os.os_mas_id "
                    + "WHERE os.os_fecha BETWEEN ? AND ? "
            );
        }

        // ---------------------------------------------------------
        // Filtros dinámicos
        // ---------------------------------------------------------
        if (numeroOrden != null && !numeroOrden.isEmpty()) {
            sql.append(" AND os.os_id = ?");
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND os.os_estado = ?");
        }
        if (nombreVeterinario != null && !nombreVeterinario.isEmpty()) {
            sql.append(" AND v.vet_nombre LIKE ?");
        }
        if (nombreMascota != null && !nombreMascota.isEmpty()) {
            sql.append(" AND m.mas_nombre LIKE ?");
        }
        if (filtrandoExamen) {
            sql.append(" AND e.exa_descripcion = ?");
        }

        sql.append(" ORDER BY os.os_id ASC");

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            ps.setDate(paramIndex++, new java.sql.Date(fechaInicio.getTime()));
            ps.setDate(paramIndex++, new java.sql.Date(fechaFin.getTime()));

            if (numeroOrden != null && !numeroOrden.isEmpty()) {
                ps.setInt(paramIndex++, Integer.parseInt(numeroOrden));
            }

            if (estado != null && !estado.isEmpty()) {
                ps.setString(paramIndex++, estado);
            }

            if (nombreVeterinario != null && !nombreVeterinario.isEmpty()) {
                ps.setString(paramIndex++, "%" + nombreVeterinario + "%");
            }

            if (nombreMascota != null && !nombreMascota.isEmpty()) {
                ps.setString(paramIndex++, "%" + nombreMascota + "%");
            }

            if (filtrandoExamen) {
                ps.setString(paramIndex++, nombreExamen);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                OrdenServicio o = new OrdenServicio();
                o.setId(rs.getInt("os_id"));
                o.setFecha(rs.getDate("os_fecha"));
                o.setVeterinario(rs.getString("veterinario"));
                o.setMascota(rs.getString("mascota"));

                o.setNombreExamen(rs.getString("examen"));

                o.setTotal(rs.getDouble("total"));
                o.setAbono(rs.getDouble("abono"));
                o.setSaldo(rs.getDouble("saldo"));
                o.setEstado(rs.getString("estado"));

                lista.add(o);
            }

        } catch (SQLException e) {
            System.err.println(" Error al listar órdenes: " + e.getMessage());
        }

        return lista;
    }

    // ======================================================
    // CABECERA POR ID
    // ======================================================
    public OrdenServicio obtenerCabeceraPorId(int osId) {
        OrdenServicio o = null;

        String sql = "SELECT os.os_id, os.os_fecha, os.os_mas_propietario, "
                + "os.os_estado, os.os_valor_total, os.os_abono, os.os_saldo, os.os_observaciones, "
                + "v.vet_id, v.vet_nombre, v.vet_veterinaria, "
                + "m.mas_id, m.mas_nombre "
                + "FROM orden_servicio os "
                + "INNER JOIN veterinario v ON v.vet_id = os.os_vet_id "
                + "INNER JOIN mascota m ON m.mas_id = os.os_mas_id "
                + "WHERE os.os_id = ?";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, osId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                o = new OrdenServicio();
                o.setId(rs.getInt("os_id"));
                o.setFecha(rs.getDate("os_fecha"));
                o.setPropietario(rs.getString("os_mas_propietario"));
                o.setEstado(rs.getString("os_estado"));
                o.setTotal(rs.getDouble("os_valor_total"));
                o.setAbono(rs.getDouble("os_abono"));
                o.setSaldo(rs.getDouble("os_saldo"));
                o.setObservaciones(rs.getString("os_observaciones"));
                o.setIdVeterinario(rs.getInt("vet_id"));
                o.setVeterinario(rs.getString("vet_nombre"));
                o.setVeterinaria(rs.getString("vet_veterinaria"));
                o.setIdMascota(rs.getInt("mas_id"));
                o.setMascota(rs.getString("mas_nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cabecera de orden: " + e.getMessage());
        }

        return o;
    }

    // ======================================================
    // SIGUIENTE ID
    // ======================================================
    public int obtenerSiguienteId() {
        int siguienteId = 1;
        String sql = "SELECT MAX(os_id) FROM orden_servicio";

        try (Connection con = db.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                siguienteId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente ID: " + e.getMessage());
        }

        return siguienteId;
    }

    // ======================================================
    // ACTUALIZAR ESTADO
    // ======================================================
    public boolean actualizarEstado(int osId, String nuevoEstado) {

        try (Connection conn = MySQLConnection.getConexion()) {

            String sqlVerificar = "SELECT os_estado FROM orden_servicio WHERE os_id = ?";
            PreparedStatement ps = conn.prepareStatement(sqlVerificar);
            ps.setInt(1, osId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estadoActual = rs.getString("os_estado");

                if ("Facturado".equalsIgnoreCase(estadoActual)
                        && "Facturado".equalsIgnoreCase(nuevoEstado)) {
                    return false;
                }
            }

            rs.close();
            ps.close();

            String sqlActualizar = "UPDATE orden_servicio SET os_estado = ? WHERE os_id = ?";
            ps = conn.prepareStatement(sqlActualizar);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, osId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }
}
