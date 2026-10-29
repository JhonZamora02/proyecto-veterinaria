/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.*;
import Model.*;
import View.*;

import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

// Importar solo lo necesario de java.util (evitar conflictos con OpenPDF)
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.Component;
import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

// Librerias OpenPDF (importar solo clases necesarias)
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.Element;

import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;


public class CtrlOrdenServicioDetalle implements ActionListener {

    private final OrdenServicioDAO daoOrden;
    private final VeterinarioDAO daoVeterinario;
    private final MascotaDAO daoMascota;
    private final OrdenServicioDetalleDAO daoDetalle;

    private frmListarOrdenServicio vista;
    private frmMenu menuAnterior;
    private String usuarioLogueado;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Constructor
    public CtrlOrdenServicioDetalle(frmListarOrdenServicio vista, frmMenu menuAnterior, String usuarioLogueado) {
        this.daoOrden = new OrdenServicioDAO();
        this.daoVeterinario = new VeterinarioDAO();
        this.daoMascota = new MascotaDAO();
        this.daoDetalle = new OrdenServicioDetalleDAO();
        this.vista = vista;
        this.menuAnterior = menuAnterior;
        this.usuarioLogueado = usuarioLogueado;

        vista.setLocationRelativeTo(null);
        vista.setResizable(false);
        vista.setTitle("Listado de Ordenes de Servicio - VetSoft v1.0");

        // Eventos
        this.vista.btnRegresa.addActionListener(this);
        this.vista.btnBuscar.addActionListener(this);
        this.vista.btnVerDetalle.addActionListener(this);
        this.vista.btnFacturar.addActionListener(this);
        this.vista.btnExportarListar.addActionListener(this);

        // Inicializacion
        cargarComboVeterinarios();
        cargarComboPropietarios();
        establecerFechasHoy();
        inicializarTabla();

        // Colorear filas facturadas
        vista.tblOrdenes.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String estado = table.getValueAt(row, 9).toString();

                if ("Facturado".equalsIgnoreCase(estado)) {
                    if (!isSelected) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(new Color(0, 100, 0));
                    }
                } else {
                    if (!isSelected) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }

                return c;
            }
        });

        vista.lblRegistros.setText("Mostrando 0 registros.");
    }

    // Eventos principales
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.btnRegresa) {
            regresarAOrdenServicio();
        }

        if (e.getSource() == vista.btnBuscar) {
            buscarOrdenes();
        }

        if (e.getSource() == vista.btnVerDetalle) {
            verDetalleSeleccionado();
        }

        if (e.getSource() == vista.btnFacturar) {
            facturarOrdenSeleccionada();
        }

        if (e.getSource() == vista.btnExportarListar) {
            exportarPDF_Listado();
        }
    }

    // Cargar combo de veterinarios
    private void cargarComboVeterinarios() {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Todos");

        for (Object[] v : daoVeterinario.listarTodos()) {
            if ("Activo".equalsIgnoreCase(v[6].toString())) {
                modelo.addElement(v[1].toString());
            }
        }

        vista.cmbVeterinarioListar.setModel(modelo);
        vista.cmbVeterinarioListar.setSelectedIndex(0);
    }

    // Cargar combo de propietarios
    private void cargarComboPropietarios() {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Todos");

        Set<String> propietarios = new HashSet<>();

        for (Mascota m : daoMascota.listarTodas()) {
            if (m.getPropietario() != null && !m.getPropietario().isEmpty()) {
                propietarios.add(m.getPropietario());
            }
        }

        for (String p : propietarios) {
            modelo.addElement(p);
        }

        vista.cmbPropietarioListar.setModel(modelo);
        vista.cmbPropietarioListar.setSelectedIndex(0);
    }

    // Asignar fecha actual
    private void establecerFechasHoy() {
        String hoy = sdf.format(new Date());
        vista.txtFechaInicio.setText(hoy);
        vista.txtFechaFin.setText(hoy);
    }

    // Inicializar tabla
    private void inicializarTabla() {
        String[] columnas = {
            "N° Orden", "Fecha", "Veterinario", "Veterinaria",
            "Mascota", "Propietario", "Valor Total", "Abono", "Saldo", "Estado"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        vista.tblOrdenes.setModel(modelo);
    }

    // Buscar ordenes
    private void buscarOrdenes() {
        try {
            String fechaInicioStr = vista.txtFechaInicio.getText().trim();
            String fechaFinStr = vista.txtFechaFin.getText().trim();

            if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe ingresar las fechas de inicio y fin.");
                return;
            }

            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);

            String veterinario = vista.cmbVeterinarioListar.getSelectedItem().toString();
            if ("Todos".equalsIgnoreCase(veterinario)) veterinario = "";

            String propietario = vista.cmbPropietarioListar.getSelectedItem().toString();
            if ("Todos".equalsIgnoreCase(propietario)) propietario = "";

            String numeroOrden = vista.txtNumero.getText().trim();

            List<OrdenServicio> lista = daoOrden.listarOrdenes(
                    fechaInicio, fechaFin, veterinario, numeroOrden, propietario);

            cargarTabla(lista);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al buscar ordenes: " + e.getMessage());
        }
    }

    // Cargar informacion en la tabla
    private void cargarTabla(List<OrdenServicio> lista) {

        DefaultTableModel modelo = (DefaultTableModel) vista.tblOrdenes.getModel();
        modelo.setRowCount(0);

        if (lista == null || lista.isEmpty()) {
            vista.lblRegistros.setText("No se encontraron registros.");
            return;
        }

        for (OrdenServicio o : lista) {
            modelo.addRow(new Object[]{
                o.getId(),
                sdf.format(o.getFecha()),
                o.getVeterinario(),
                o.getVeterinaria(),
                o.getMascota(),
                o.getPropietario(),
                o.getTotal(),
                o.getAbono(),
                o.getSaldo(),
                o.getEstado()
            });
        }

        vista.lblRegistros.setText("Mostrando " + lista.size() + " registros encontrados.");
    }

    // Ver detalle
    private void verDetalleSeleccionado() {
        int fila = vista.tblOrdenes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una orden.");
            return;
        }

        try {
            int osId = Integer.parseInt(vista.tblOrdenes.getValueAt(fila, 0).toString());
            OrdenServicio orden = daoOrden.obtenerCabeceraPorId(osId);

            vista.dispose();

            frmOrdenServicio vistaOrden = new frmOrdenServicio();
            CtrlOrdenServicio ctrlOrden = new CtrlOrdenServicio(vistaOrden, menuAnterior, usuarioLogueado);

            ctrlOrden.mostrarOrdenEnVistaDesdeListado(
                    orden.getId(),
                    sdf.format(orden.getFecha()),
                    orden.getVeterinario(),
                    orden.getMascota(),
                    orden.getPropietario(),
                    orden.getTotal(),
                    orden.getAbono(),
                    orden.getSaldo(),
                    orden.getEstado()
            );

            vistaOrden.btnListar.setVisible(false);

            vistaOrden.btnRegresa.addActionListener(ev -> {
                vistaOrden.dispose();
                frmListarOrdenServicio nuevaVista = new frmListarOrdenServicio();
                CtrlOrdenServicioDetalle nuevoCtrl = new CtrlOrdenServicioDetalle(nuevaVista, menuAnterior, usuarioLogueado);
                nuevaVista.setVisible(true);
            });

            vistaOrden.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al abrir el detalle.");
        }
    }

    // Facturar
    private void facturarOrdenSeleccionada() {
        int fila = vista.tblOrdenes.getSelectedRow();
        
        // 1. VALIDAR QUE HAYA UNA FILA SELECCIONADA
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                "Por favor seleccione una orden de servicio para facturar.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. OBTENER DATOS DE LA FILA SELECCIONADA
            int osId = Integer.parseInt(vista.tblOrdenes.getValueAt(fila, 0).toString());
            String estadoActual = vista.tblOrdenes.getValueAt(fila, 9).toString().trim();

            // 3. VALIDAR QUE NO ESTÉ YA FACTURADA
            if ("Facturado".equalsIgnoreCase(estadoActual)) {
                JOptionPane.showMessageDialog(vista,
                    "⚠️ Esta orden ya fue facturada previamente.\n\n" +
                    "Orden N°: " + osId + "\n" +
                    "Estado: " + estadoActual + "\n\n" +
                    "No es posible facturar una orden más de una vez.",
                    "Orden Ya Facturada",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4. VALIDAR QUE EL SALDO NO SEA CERO (OPCIONAL)
            double saldo = toDoubleSafe(vista.tblOrdenes.getValueAt(fila, 8));
            if (saldo == 0) {
                int continuar = JOptionPane.showConfirmDialog(vista,
                    "El saldo de esta orden es $0.00\n" +
                    "¿Desea continuar con la facturación?",
                    "Saldo en Cero",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (continuar != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // 5. GENERAR LA FACTURA EN PDF/VISTA
            boolean facturaGenerada = generarFacturaPDF(osId);

            if (!facturaGenerada) {
                return; // Ya se mostró el error en el método
            }

            // 6. CONFIRMAR LA FACTURACIÓN
            int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Desea confirmar la facturación de esta orden de servicio?\n\n" +
                "Orden N°: " + osId + "\n" +
                "Estado Actual: " + estadoActual + "\n" +
                "Nuevo Estado: Facturado\n\n" +
                "⚠️ Esta acción NO se puede deshacer.",
                "Confirmar Facturación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            // 7. CAMBIAR EL ESTADO SI CONFIRMA
            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean actualizado = daoOrden.actualizarEstado(osId, "Facturado");

                if (actualizado) {
                    JOptionPane.showMessageDialog(vista,
                        "✓ La orden ha sido facturada exitosamente.\n\n" +
                        "Orden N°: " + osId + "\n" +
                        "Nuevo Estado: Facturado",
                        "Facturación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Actualizar la tabla para reflejar el cambio
                    vista.tblOrdenes.setValueAt("Facturado", fila, 9);
                    
                    // Opcional: Cambiar color de la fila para indicar facturada
                    // vista.tblOrdenes.repaint();
                    
                } else {
                    JOptionPane.showMessageDialog(vista,
                        "❌ No se pudo actualizar el estado de la orden.\n\n" +
                        "Por favor verifique la conexión a la base de datos\n" +
                        "e intente nuevamente.",
                        "Error al Actualizar",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Usuario canceló la facturación
                JOptionPane.showMessageDialog(vista,
                    "La facturación fue cancelada.\n" +
                    "El estado de la orden permanece sin cambios.",
                    "Facturación Cancelada",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista,
                "Error al leer los datos de la orden seleccionada.\n" +
                "Por favor intente nuevamente.",
                "Error de Datos",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista,
                "Error al procesar la facturación:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Jasper
    private boolean generarFacturaPDF(int osId) {
        Connection conn = null;
        try {
            String reportPath = System.getProperty("user.dir") + "/src/Report/Factura7.0.2.jasper";
            java.io.File file = new java.io.File(reportPath);

            if (!file.exists()) {
                JOptionPane.showMessageDialog(vista, "No se encontro el archivo del reporte.");
                return false;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/labpets", "root", "");

            Map<String, Object> params = new HashMap<>();
            params.put("os_id", osId);

            JasperPrint print = JasperFillManager.fillReport(reportPath, params, conn);

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setVisible(true);

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error Jasper.");
            return false;

        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {}
        }
    }

    // Exportar listado a PDF
    private void exportarPDF_Listado() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) vista.tblOrdenes.getModel();

            if (modelo.getRowCount() == 0) {
                JOptionPane.showMessageDialog(vista, "No hay informacion para exportar.");
                return;
            }

            String fechaIni = vista.txtFechaInicio.getText();
            String fechaFin = vista.txtFechaFin.getText();

            String nombreArchivo = System.getProperty("user.home") + "/Downloads/Listado_OrdenesDeServicio" + fechaIni + "_" + fechaFin + ".pdf";

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();

            Paragraph titulo = new Paragraph("Reporte Consolidado de Ordenes de Servicio",
                    new Font(Font.HELVETICA, 16, Font.BOLD));
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Periodo de consulta:"));
            doc.add(new Paragraph("Desde: " + fechaIni + " - Fecha fin: " + fechaFin));
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(modelo.getColumnCount());
            tabla.setWidthPercentage(100);

            for (int i = 0; i < modelo.getColumnCount(); i++) {
                tabla.addCell(new PdfPCell(new Phrase(modelo.getColumnName(i))));
            }

            for (int i = 0; i < modelo.getRowCount(); i++) {
                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    tabla.addCell(modelo.getValueAt(i, j).toString());
                }
            }

            doc.add(tabla);
            doc.close();

            JOptionPane.showMessageDialog(vista, "Reporte exportado en Descargas.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al exportar PDF: " + e.getMessage());
        }
    }

    // Regresar
    private void regresarAOrdenServicio() {
        vista.dispose();
        frmOrdenServicio vistaOrden = new frmOrdenServicio();
        new CtrlOrdenServicio(vistaOrden, menuAnterior, usuarioLogueado);
        vistaOrden.setVisible(true);
    }

    // Auxiliar
    private double toDoubleSafe(Object v) {
        try {
            return Double.parseDouble(v.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
