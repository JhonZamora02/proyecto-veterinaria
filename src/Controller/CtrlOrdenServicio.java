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
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

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
        

        // ====== Num. de orden autogenerado ======
        int siguienteId = daoOrden.obtenerSiguienteId();
        vista.txtNumero.setText(String.valueOf(siguienteId));
        vista.txtNumero.setEditable(false);

        // ====== Fecha actual ======
        establecerFechaActual();
        vista.txtFechaToma.setEditable(true);

        // ====== Campos bloqueados ======
        vista.txtValor.setEditable(false);
        vista.txtTotal.setEditable(false);
        vista.txtSaldo.setEditable(false);

        // ====== Asignar eventos ======
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnCancelar.addActionListener(this);
        this.vista.btnRegresa.addActionListener(this);
        this.vista.btnAgregarExamen.addActionListener(this);
        this.vista.btnEliminarExamen.addActionListener(this);
        this.vista.btnListar.addActionListener(this);

        // ====== Inicializar combos y tabla ======
        cargarComboMascota();
        cargarComboVeterinario();
        cargarComboExamen();
        inicializarTabla();

        // ====== Eventos automáticos ======
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
    }

    // ======================================================
    // FECHA ACTUAL
    // ======================================================
    private void establecerFechaActual() {
        Date hoy = new Date();
        vista.txtFechaToma.setText(sdf.format(hoy));
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
    // EVENTOS
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
                abono = total;
                vista.txtAbono.setText(String.valueOf(total));
            }
            vista.txtSaldo.setText(String.valueOf(total - abono));
        } catch (NumberFormatException ex) {

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
            if (ex == null) {
                JOptionPane.showMessageDialog(vista, "Examen no encontrado.");
                return;
            }

            if (vista.txtCantidad.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Ingrese la cantidad del examen.");
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

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un numero válido.");
        }
    }

    private void eliminarDetalle() {
        int fila = vista.tblDetalleServicio.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un registro a eliminar.");
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

            if (((DefaultTableModel) vista.tblDetalleServicio.getModel()).getRowCount() == 0) {
                JOptionPane.showMessageDialog(vista, "Agregue al menos un examen al detalle.");
                return;
            }

            String mascotaCombo = vista.cmbMascota.getSelectedItem().toString();
            String nombreMascota = mascotaCombo.split(" - ")[0].trim();
            String propietario = extraerPropietarioDeComboMascota(mascotaCombo);

            String veterinarioCombo = vista.cmbVeterinario.getSelectedItem().toString();
            String nombreVeterinario = veterinarioCombo.split(" - ")[0].trim();

            int idMascota = obtenerIdMascotaPorNombre(nombreMascota);
            int idVeterinario = obtenerIdVeterinarioPorNombre(nombreVeterinario);

            if (idMascota == 0) {
                JOptionPane.showMessageDialog(vista, "No se pudo resolver el ID de la mascota seleccionada.");
                return;
            }
            if (idVeterinario == 0) {
                JOptionPane.showMessageDialog(vista, "No se pudo resolver el ID del veterinario seleccionado.");
                return;
            }

            Date fecha;
            try {
                sdf.setLenient(false);
                fecha = sdf.parse(vista.txtFechaToma.getText().trim());
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(vista, "La fecha debe tener formato yyyy-MM-dd.");
                return;
            }

            double total = vista.txtTotal.getText().isEmpty() ? 0.0 : Double.parseDouble(vista.txtTotal.getText());
            double abono = vista.txtAbono.getText().isEmpty() ? 0.0 : Double.parseDouble(vista.txtAbono.getText());
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
                JOptionPane.showMessageDialog(vista, "Orden registrada correctamente (ID: " + idGenerado + ")");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo registrar la orden.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar orden: " + ex.getMessage());
        }
    }

    private void guardarDetallesOrden(int idOrden) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleServicio.getModel();
        List<OrdenServicioDetalle> lista = new ArrayList<>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            OrdenServicioDetalle det = new OrdenServicioDetalle();
            String nombreExamen = modelo.getValueAt(i, 0).toString();
            Examen ex = buscarExamenPorDescripcion(nombreExamen);
            if (ex == null) {
                continue;
            }
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
    // UTILIDADES
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
    }

    private void regresarAlMenu() {
        vista.dispose();
        if (menuAnterior.lblNombreUsuario != null) {
            menuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
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
        int id = 0;
        for (Object[] v : daoVeterinario.listarTodos()) {
            String nombreVet = v[1].toString().trim().toLowerCase();
            if (nombreBuscado.toLowerCase().startsWith(nombreVet)
                    || nombreVet.startsWith(nombreBuscado.toLowerCase())
                    || nombreVet.contains(nombreBuscado.toLowerCase())) {
                id = Integer.parseInt(v[0].toString());
                break;
            }
        }
        return id;
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
// MÉTODO PARA MOSTRAR DETALLE DESDE EL LISTADO
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
            // Bloquear botones que no se deben usar en modo consulta
            vista.btnGuardar.setEnabled(false);
            vista.btnAgregarExamen.setEnabled(false);
            vista.btnEliminarExamen.setEnabled(false);
            vista.btnCancelar.setEnabled(false);
            vista.btnListar.setVisible(false); // Oculta el botón Listar

            // Llenar los campos principales
            vista.txtNumero.setText(String.valueOf(osId));
            vista.txtFechaToma.setText(fechaYYYYMMDD);
            vista.txtTotal.setText(String.valueOf(total));
            vista.txtAbono.setText(String.valueOf(abono));
            vista.txtSaldo.setText(String.valueOf(saldo));
            vista.txtObservaciones.setText("");

            // Bloquear campos de edición
            vista.txtNumero.setEditable(false);
            vista.txtFechaToma.setEditable(false);
            vista.txtAbono.setEditable(false);
            vista.txtObservaciones.setEditable(false);
            vista.txtCantidad.setEditable(false);
            vista.txtValor.setEditable(false);

            vista.cmbMascota.setEnabled(false);
            vista.cmbVeterinario.setEnabled(false);
            vista.cmbExamen.setEnabled(false);

            // Cargar información del veterinario y la mascota
            vista.cmbVeterinario.removeAllItems();
            vista.cmbVeterinario.addItem(nombreVeterinario);
            vista.cmbVeterinario.setSelectedIndex(0);

            vista.cmbMascota.removeAllItems();
            vista.cmbMascota.addItem(nombreMascota + " - Propietario: " + propietario);
            vista.cmbMascota.setSelectedIndex(0);

            // Cargar detalle de exámenes desde la BD
            DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleServicio.getModel();
            modelo.setRowCount(0);

            List<OrdenServicioDetalle> detalles = daoDetalle.listarPorOrden(osId);
            if (detalles != null && !detalles.isEmpty()) {
                for (OrdenServicioDetalle d : detalles) {
                    modelo.addRow(new Object[]{
                        d.getDescripcionExamen(),
                        d.getValor(),
                        d.getCantidad(),
                        d.getObservaciones(),
                        d.getSubtotal()
                    });
                }
            }

            // boton "Regresar" para volver solo al listado
            for (ActionListener al : vista.btnRegresa.getActionListeners()) {
                vista.btnRegresa.removeActionListener(al);
            }

            vista.btnRegresa.addActionListener(ev -> {
                vista.dispose();
                frmListarOrdenServicio vistaListar = new frmListarOrdenServicio();
                CtrlOrdenServicioDetalle ctrlListar = new CtrlOrdenServicioDetalle(vistaListar, menuAnterior, usuarioLogueado);
                vistaListar.setVisible(true);
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al mostrar la orden: " + ex.getMessage());
        }
    }

}
