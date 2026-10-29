/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.FormaPagoDAO;

import Model.FormaPago;
import View.frmEspecie;
import View.frmFormaPago;
import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CtrlFormaPago implements ActionListener {

    private frmFormaPago vistaFpago;
    private FormaPagoDAO dao;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlFormaPago(frmFormaPago vistaFpago, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaFpago = vistaFpago;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new FormaPagoDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaFpago.setLocationRelativeTo(null);
        vistaFpago.setResizable(false);
        vistaFpago.setSize(680, 595);

        // eventos
        this.vistaFpago.btnAgregarPago.addActionListener(this);
        this.vistaFpago.btnActualizarPago.addActionListener(this);
        this.vistaFpago.btnEstadoPago.addActionListener(this);
        this.vistaFpago.btnLimpiarPago.addActionListener(this);
        this.vistaFpago.btnRegresa.addActionListener(this);

        // cargar tabla inicial
        cargarTabla("");

        this.vistaFpago.txtEstadoPago.setText("Activo");
        this.vistaFpago.txtEstadoPago.setEnabled(false);

        // evento de selección en la tabla
        this.vistaFpago.tblFormapago.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = vistaFpago.tblFormapago.getSelectedRow();
                if (fila != -1) {
                    vistaFpago.txtIdPago.setText(vistaFpago.tblFormapago.getValueAt(fila, 0).toString());
                    vistaFpago.txtDescripcionPago.setText(vistaFpago.tblFormapago.getValueAt(fila, 1).toString());
                    vistaFpago.txtEstadoPago.setText(vistaFpago.tblFormapago.getValueAt(fila, 2).toString());
                }
            }
        });

        inicializarConsecutivoId();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaFpago.btnAgregarPago) {
            agregar();
        }
        if (e.getSource() == vistaFpago.btnActualizarPago) {
            actualizar();
        }
        if (e.getSource() == vistaFpago.btnEstadoPago) {
            cambiarEstado();
        }
        if (e.getSource() == vistaFpago.btnLimpiarPago) {
            limpiar();
        }
        if (e.getSource() == vistaFpago.btnRegresa) {
            regresar();
        }
    }

    // ===================== MÉTODOS =====================
    private void cargarTabla(String filtro) {
        String[] cols = {"ID", "Descripción", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<FormaPago> lista = dao.listarTodas();
        for (FormaPago e : lista) {
            if (filtro.isEmpty() || e.getDescripcion().toLowerCase().contains(filtro.toLowerCase())) {
                modelo.addRow(new Object[]{e.getId(), e.getDescripcion(), e.getEstado()});
            }
        }
        vistaFpago.tblFormapago.setModel(modelo);
    }

    private void agregar() {
        try {
            int id = Integer.parseInt(vistaFpago.txtIdPago.getText().trim());
            String descripcion = vistaFpago.txtDescripcionPago.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaFpago, "La descripción no puede estar vacía.");
                return;
            }

            FormaPago e = new FormaPago();
            e.setId(id);
            e.setDescripcion(descripcion);
            e.setEstado("Activo");

            if (dao.agregar(e)) {
                JOptionPane.showMessageDialog(vistaFpago, "Forma de pago registrada correctamente.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaFpago, "Error: no se pudo registrar (ID o descripción duplicada).");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaFpago, "El ID debe ser numérico.");
        }
    }

    private void actualizar() {
        try {
            int id = Integer.parseInt(vistaFpago.txtIdPago.getText().trim());
            String descripcion = vistaFpago.txtDescripcionPago.getText().trim();
            String estado = vistaFpago.txtEstadoPago.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaFpago, "La descripción no puede estar vacía.");
                return;
            }

            FormaPago e = new FormaPago(id, descripcion, estado);
            if (dao.actualizar(e)) {
                JOptionPane.showMessageDialog(vistaFpago, "Forma de pago actualizada.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaFpago, "Error al actualizar.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaFpago, "El ID debe ser numérico.");
        }
    }

    private void cambiarEstado() {
        int fila = vistaFpago.tblFormapago.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaFpago, "Seleccione una forma de pago primero.");
            return;
        }
        int id = (int) vistaFpago.tblFormapago.getValueAt(fila, 0);
        String estadoActual = vistaFpago.tblFormapago.getValueAt(fila, 2).toString();
        String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Anulado" : "Activo";

        if (dao.cambiarEstado(id, nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaFpago, "Estado cambiado a " + nuevoEstado);
            cargarTabla("");
            limpiar();
        } else {
            JOptionPane.showMessageDialog(vistaFpago, "No se pudo cambiar el estado.");
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaFpago.txtIdPago.setText(String.valueOf(siguienteId));
        vistaFpago.txtIdPago.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaFpago.txtDescripcionPago.setText("");
        vistaFpago.txtEstadoPago.setText("Activo");
        inicializarConsecutivoId();

    }
    // ===================== REGRESAR AL MENÚ =====================

    private void regresar() {
        vistaFpago.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
