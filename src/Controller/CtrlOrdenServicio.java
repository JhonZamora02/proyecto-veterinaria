/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.*;
import Model.*;
import View.*;

import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.Connection;
import java.sql.DriverManager;

public class CtrlOrdenServicio implements ActionListener {

    private frmOrdenServicio vista;
    private OrdenServicioDAO daoOrden;
    private OrdenServicioDetalleDAO daoDetalle;
    private MascotaDAO daoMascota;
    private VeterinarioDAO daoVeterinario;
    private ExamenDAO daoExamen;
    private frmMenu menuAnterior;
    private String usuarioLogueado;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // ======================================================
    // CONSTRUCTOR
    // ======================================================
    public CtrlOrdenServicio(frmOrdenServicio vista, frmMenu menuAnterior, String usuarioLogueado) {
        this.vista = vista;
        this.menuAnterior = menuAnterior;
        this.usuarioLogueado = usuarioLogueado;

        this.daoOrden = new OrdenServicioDAO();
        this.daoDetalle = new OrdenServicioDetalleDAO();
        this.daoMascota = new MascotaDAO();
        this.daoVeterinario = new VeterinarioDAO();
        this.daoExamen = new ExamenDAO();

        vista.setLocationRelativeTo(null);
        vista.setResizable(false);
        vista.setTitle("Orden de Servicio - VetSoft v1.0");

        // El botón Exportar SIEMPRE debe estar oculto al crear una orden nueva
        vista.btnExportar.setVisible(false);

        int siguienteId = daoOrden.obtenerSiguienteId();
        vista.txtNumero.setText(String.valueOf(siguienteId));
        vista.txtNumero.setEditable(false);

        establecerFechaActual();
        vista.txtFechaToma.setEditable(true);

        vista.txtValor.setEditable(false);
        vista.txtTotal.setEditable(false);
        vista.txtSaldo.setEditable(false);

        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        vista.btnRegresa.addActionListener(this);
        vista.btnAgregarExamen.addActionListener(this);
        vista.btnEliminarExamen.addActionListener(this);
        vista.btnListar.addActionListener(this);
        vista.btnExportar.addActionListener(this);

        cargarComboMascota();
        cargarComboVeterinario();
        cargarComboExamen();
        inicializarTabla();

        inicializarEventoValorExamen();
        inicializarEventoAbono();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.btnGuardar) {
            guardarOrdenServicio();
        }
        if (e.getSource() == vista.btnCancelar) {
            limpiarCampos();
        }
        if (e.getSource() == vista.btnAgregarExamen) {
            agregarDetalle();
        }
        if (e.getSource() == vista.btnEliminarExamen) {
            eliminarDetalle();
        }
        if (e.getSource() == vista.btnRegresa) {
            regresarAlMenu();
        }
        if (e.getSource() == vista.btnListar) {
            abrirListadoOrdenes();
        }
        if (e.getSource() == vista.btnExportar) {
            exportarFacturaActual();
        }
    }

    // ======================================================
    // EXPORTAR FACTURA DE LA ORDEN ACTUAL
    // ======================================================
    private void exportarFacturaActual() {
        try {

            int osId = Integer.parseInt(vista.txtNumero.getText());

            String ruta = System.getProperty("user.dir") + "/src/Report/Factura_OrdenServicio.jasper";

            java.io.File f = new java.io.File(ruta);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(vista, "No se encontró el archivo del reporte Jasper:\n" + ruta);
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/labpets", "root", ""
            );

            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("idOrden", osId);

            JasperPrint print = JasperFillManager.fillReport(ruta, params, conn);
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setVisible(true);

            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error generando factura Jasper:\n" + ex.getMessage());
        }
    }

    // ======================================================
    // FECHA ACTUAL
    // ======================================================
    private void establecerFechaActual() {
        vista.txtFechaToma.setText(sdf.format(new Date()));
    }

    // ======================================================
    // COMBOS
    // ======================================================
    private void cargarComboMascota() {
        vista.cmbMascota.removeAllItems();
        for (Mascota m : daoMascota.listarTodas()) {
            if ("Activo".equalsIgnoreCase(m.getEstado())) {
                vista.cmbMascota.addItem(m.getNombre() + " - Propietario: " + m.getPropietario());
            }
        }
        vista.cmbMascota.setSelectedIndex(-1);
    }

    private void cargarComboVeterinario() {
        vista.cmbVeterinario.removeAllItems();
        for (Object[] v : daoVeterinario.listarTodos()) {
            if ("Activo".equalsIgnoreCase(v[6].toString())) {
                vista.cmbVeterinario.addItem(v[1] + " - Veterinaria: " + v[4]);
            }
        }
        vista.cmbVeterinario.setSelectedIndex(-1);
    }

    private void cargarComboExamen() {
        vista.cmbExamen.removeAllItems();
        for (Examen ex : daoExamen.listar()) {
            if ("Activo".equalsIgnoreCase(ex.getEstado())) {
                vista.cmbExamen.addItem(ex.getDescripcion());
            }
        }
        vista.cmbExamen.setSelectedIndex(-1);
    }

    // ======================================================
    // TABLA
    // ======================================================
    private void inicializarTabla() {
        String[] columnas = {"Examen", "Valor", "Cantidad", "Observaciones", "Subtotal"};

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        vista.tblDetalleServicio.setModel(modelo);
    }

    // ======================================================
    // EVENTOS AUTOMÁTICOS
    // ======================================================
    private void inicializarEventoValorExamen() {
        vista.cmbExamen.addActionListener(e -> mostrarValorExamenSeleccionado());
    }

    private void mostrarValorExamenSeleccionado() {
        try {
            if (vista.cmbExamen.getSelectedItem() == null) {
                vista.txtValor.setText("");
                return;
            }

            String nombreExamen = vista.cmbExamen.getSelectedItem().toString();
            Examen ex = buscarExamenPorDescripcion(nombreExamen);

            vista.txtValor.setText(ex != null ? String.valueOf(ex.getValor()) : "");

        } catch (Exception e) {
            vista.txtValor.setText("");
        }
    }

    private void inicializarEventoAbono() {
        vista.txtAbono.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                recalcularSaldo();
            }

            public void removeUpdate(DocumentEvent e) {
                recalcularSaldo();
            }

            public void changedUpdate(DocumentEvent e) {
                recalcularSaldo();
            }
        });
    }

    private void recalcularSaldo() {
        try {
            double total = vista.txtTotal.getText().isEmpty() ? 0.0 : Double.parseDouble(vista.txtTotal.getText());
            double abono = vista.txtAbono.getText().isEmpty() ? 0.0 : Double.parseDouble(vista.txtAbono.getText());

            if (abono < 0) {
                abono = 0;
            }
            if (abono > total) {
                JOptionPane.showMessageDialog(vista, "El abono no puede ser mayor que el total.");
                vista.txtAbono.setText(String.valueOf(total));
                abono = total;
            }

            vista.txtSaldo.setText(String.valueOf(total - abono));

        } catch (Exception e) {
        }
    }

    // ======================================================
    // DETALLE DE EXÁMENES
    // ======================================================
    private void agregarDetalle() {
        try {
            if (vista.cmbExamen.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(vista, "Seleccione un examen.");
                return;
            }

            String nombreExamen = vista.cmbExamen.getSelectedItem().toString();
            Examen ex = buscarExamenPorDescripcion(nombreExamen);

            if (vista.txtCantidad.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Ingrese la cantidad.");
                return;
            }

            int cantidad = Integer.parseInt(vista.txtCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.");
                return;
            }

            String observaciones = vista.txtObservaciones.getText().trim();
            double subtotal = cantidad * ex.getValor();

            DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleServicio.getModel();
            modelo.addRow(new Object[]{nombreExamen, ex.getValor(), cantidad, observaciones, subtotal});

            actualizarTotales();
            limpiarCamposDetalle();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Cantidad invalida.");
        }
    }

    private void eliminarDetalle() {
        int fila = vista.tblDetalleServicio.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un examen.");
            return;
        }

        ((DefaultTableModel) vista.tblDetalleServicio.getModel()).removeRow(fila);
        actualizarTotales();
    }

    private void actualizarTotales() {
        double total = 0;
        DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleServicio.getModel();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += Double.parseDouble(modelo.getValueAt(i, 4).toString());
        }

        vista.txtTotal.setText(String.valueOf(total));
        recalcularSaldo();
    }

    // ======================================================
    // GUARDAR ORDEN
    // ======================================================
    private void guardarOrdenServicio() {
        try {

            if (vista.cmbMascota.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(vista, "Seleccione una mascota.");
                return;
            }
            if (vista.cmbVeterinario.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(vista, "Seleccione un veterinario.");
                return;
            }
            if (vista.tblDetalleServicio.getRowCount() == 0) {
                JOptionPane.showMessageDialog(vista, "Agregue al menos un examen.");
                return;
            }

            String mascotaCombo = vista.cmbMascota.getSelectedItem().toString();
            String nombreMascota = mascotaCombo.split(" - ")[0].trim();
            String propietario = extraerPropietarioDeComboMascota(mascotaCombo);

            String veterinarioCombo = vista.cmbVeterinario.getSelectedItem().toString();
            String nombreVeterinario = veterinarioCombo.split(" - ")[0].trim();

            int idMascota = obtenerIdMascotaPorNombre(nombreMascota);
            int idVeterinario = obtenerIdVeterinarioPorNombre(nombreVeterinario);

            Date fecha = new Date();

            double total = vista.txtTotal.getText().isEmpty() ? 0.0
                    : Double.parseDouble(vista.txtTotal.getText());

            double abono = vista.txtAbono.getText().isEmpty() ? 0.0
                    : Double.parseDouble(vista.txtAbono.getText());

            double saldo = total - abono;

            OrdenServicio orden = new OrdenServicio();
            orden.setId(Integer.parseInt(vista.txtNumero.getText()));
            orden.setFecha(fecha);
            orden.setIdMascota(idMascota);
            orden.setIdVeterinario(idVeterinario);
            orden.setPropietario(propietario);
            orden.setEstado("Activo");
            orden.setTotal(total);
            orden.setAbono(abono);
            orden.setSaldo(saldo);
            orden.setObservaciones(vista.txtObservaciones.getText().trim());
            orden.setUsuarioCreador(usuarioLogueado);

            int idGenerado = daoOrden.guardarOrden(orden);

            if (idGenerado > 0) {
                guardarDetallesOrden(idGenerado);
                JOptionPane.showMessageDialog(vista, "Orden registrada correctamente");
                limpiarCampos();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }

    private void guardarDetallesOrden(int idOrden) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleServicio.getModel();
        List<OrdenServicioDetalle> lista = new ArrayList<>();

        for (int i = 0; i < modelo.getRowCount(); i++) {

            OrdenServicioDetalle det = new OrdenServicioDetalle();

            String nombreExamen = modelo.getValueAt(i, 0).toString();
            Examen ex = buscarExamenPorDescripcion(nombreExamen);

            det.setIdExamen(ex.getId());
            det.setValor(Double.parseDouble(modelo.getValueAt(i, 1).toString()));
            det.setCantidad(modelo.getValueAt(i, 2).toString());
            det.setObservaciones(modelo.getValueAt(i, 3).toString());
            det.setSubtotal(Double.parseDouble(modelo.getValueAt(i, 4).toString()));

            lista.add(det);
        }

        daoDetalle.guardarDetalles(lista, idOrden);
    }

    // ======================================================
    // LIMPIEZA Y NAVEGACIÓN
    // ======================================================
    private void limpiarCamposDetalle() {
        vista.txtCantidad.setText("");
        vista.txtObservaciones.setText("");
        vista.cmbExamen.setSelectedIndex(-1);
        vista.txtValor.setText("");
    }

    private void limpiarCampos() {
        inicializarTabla();
        vista.txtCantidad.setText("");
        vista.txtObservaciones.setText("");
        vista.txtTotal.setText("");
        vista.txtAbono.setText("");
        vista.txtSaldo.setText("");
        vista.cmbExamen.setSelectedIndex(-1);
        vista.cmbMascota.setSelectedIndex(-1);
        vista.cmbVeterinario.setSelectedIndex(-1);

        vista.txtNumero.setText(String.valueOf(daoOrden.obtenerSiguienteId()));
        establecerFechaActual();

        // Siempre ocultar cuando es modo creación
        vista.btnExportar.setVisible(false);
    }

    private void regresarAlMenu() {
        vista.dispose();
        menuAnterior.setVisible(true);
    }

    private void abrirListadoOrdenes() {
        vista.dispose();
        frmListarOrdenServicio vistaListar = new frmListarOrdenServicio();
        CtrlOrdenServicioDetalle ctrl = new CtrlOrdenServicioDetalle(vistaListar, menuAnterior, usuarioLogueado);
        vistaListar.setVisible(true);
    }

    // ======================================================
    // AUXILIARES
    // ======================================================
    private int obtenerIdMascotaPorNombre(String nombre) {
        for (Mascota m : daoMascota.listarTodas()) {
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                return m.getId();
            }
        }
        return 0;
    }

    private int obtenerIdVeterinarioPorNombre(String nombreBuscado) {
        for (Object[] v : daoVeterinario.listarTodos()) {
            String nombreVet = v[1].toString().trim().toLowerCase();
            if (nombreBuscado.toLowerCase().startsWith(nombreVet)
                    || nombreVet.startsWith(nombreBuscado.toLowerCase())
                    || nombreVet.contains(nombreBuscado.toLowerCase())) {
                return Integer.parseInt(v[0].toString());
            }
        }
        return 0;
    }

    private String extraerPropietarioDeComboMascota(String textoCombo) {
        int idx = textoCombo.indexOf("Propietario:");
        if (idx == -1) {
            return "";
        }
        return textoCombo.substring(idx + "Propietario:".length()).trim();
    }

    private Examen buscarExamenPorDescripcion(String descripcion) {
        for (Examen ex : daoExamen.listar()) {
            if (ex.getDescripcion().equalsIgnoreCase(descripcion)) {
                return ex;
            }
        }
        return null;
    }

    // ======================================================
    // MODO CONSULTA DESDE EL LISTADO (MUESTRA DETALLE)
    // ======================================================
    public void mostrarOrdenEnVistaDesdeListado(
            int osId,
            String fechaYYYYMMDD,
            String nombreVeterinario,
            String nombreMascota,
            String propietario,
            double total,
            double abono,
            double saldo,
            String estado
    ) {
        try {

            vista.btnGuardar.setEnabled(false);
            vista.btnAgregarExamen.setEnabled(false);
            vista.btnEliminarExamen.setEnabled(false);
            vista.btnCancelar.setEnabled(false);
            vista.btnListar.setVisible(false);

            // AHORA SI SE DEBE MOSTRAR EL BOTÓN EXPORTAR
            vista.btnExportar.setVisible(true);

            vista.txtNumero.setText(String.valueOf(osId));
            vista.txtFechaToma.setText(fechaYYYYMMDD);
            vista.txtTotal.setText(String.valueOf(total));
            vista.txtAbono.setText(String.valueOf(abono));
            vista.txtSaldo.setText(String.valueOf(saldo));
            vista.txtObservaciones.setText("");

            vista.txtNumero.setEditable(false);
            vista.txtFechaToma.setEditable(false);
            vista.txtAbono.setEditable(false);
            vista.txtObservaciones.setEditable(false);
            vista.txtCantidad.setEditable(false);
            vista.txtValor.setEditable(false);

            vista.cmbMascota.setEnabled(false);
            vista.cmbVeterinario.setEnabled(false);
            vista.cmbExamen.setEnabled(false);

            vista.cmbVeterinario.removeAllItems();
            vista.cmbVeterinario.addItem(nombreVeterinario);

            vista.cmbMascota.removeAllItems();
            vista.cmbMascota.addItem(nombreMascota + " - Propietario: " + propietario);

            DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleServicio.getModel();
            modelo.setRowCount(0);

            List<OrdenServicioDetalle> detalles = daoDetalle.listarPorOrden(osId);
            for (OrdenServicioDetalle d : detalles) {
                modelo.addRow(new Object[]{
                    d.getDescripcionExamen(),
                    d.getValor(),
                    d.getCantidad(),
                    d.getObservaciones(),
                    d.getSubtotal()
                });
            }

            for (ActionListener al : vista.btnRegresa.getActionListeners()) {
                vista.btnRegresa.removeActionListener(al);
            }

            vista.btnRegresa.addActionListener(ev -> {
                vista.dispose();
                frmListarOrdenServicio listado = new frmListarOrdenServicio();
                CtrlOrdenServicioDetalle ctrl = new CtrlOrdenServicioDetalle(listado, menuAnterior, usuarioLogueado);
                listado.setVisible(true);
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al mostrar la orden.");
        }
    }
}
