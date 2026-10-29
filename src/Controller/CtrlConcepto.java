/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.ConceptoDAO;
import Model.Concepto;

import View.frmConcepto;

import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CtrlConcepto implements ActionListener {

    private frmConcepto vistaConcepto;
    private ConceptoDAO dao;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlConcepto(frmConcepto vistaConcepto, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaConcepto = vistaConcepto;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new ConceptoDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaConcepto.setLocationRelativeTo(null);
        vistaConcepto.setResizable(false);
        vistaConcepto.setSize(698, 570);

        // eventos
        this.vistaConcepto.btnAgregarConcepto.addActionListener(this);
        this.vistaConcepto.btnActualizarConcepto.addActionListener(this);
        this.vistaConcepto.btnEstadoConcepto.addActionListener(this);
        this.vistaConcepto.btnLimpiarConcepto.addActionListener(this);
        this.vistaConcepto.btnRegresa.addActionListener(this);

        // cargar tabla inicial
        cargarTabla("");

        this.vistaConcepto.txtEstadoConcepto.setText("Activo");
        this.vistaConcepto.txtEstadoConcepto.setEnabled(false);

        // evento de selección en la tabla
        this.vistaConcepto.tblConcepto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = vistaConcepto.tblConcepto.getSelectedRow();
                if (fila != -1) {
                    vistaConcepto.txtIdConcepto.setText(vistaConcepto.tblConcepto.getValueAt(fila, 0).toString());
                    vistaConcepto.txtDescripcionConcepto.setText(vistaConcepto.tblConcepto.getValueAt(fila, 1).toString());
                    vistaConcepto.txtEstadoConcepto.setText(vistaConcepto.tblConcepto.getValueAt(fila, 2).toString());
                }
            }
        });

        inicializarConsecutivoId();

    }

    @Override

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaConcepto.btnAgregarConcepto) {
            agregar();
        }
        if (e.getSource() == vistaConcepto.btnActualizarConcepto) {
            actualizar();
        }
        if (e.getSource() == vistaConcepto.btnEstadoConcepto) {
            cambiarEstado();
        }
        if (e.getSource() == vistaConcepto.btnLimpiarConcepto) {
            limpiar();
        }
        if (e.getSource() == vistaConcepto.btnRegresa) {
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

        List<Concepto> lista = dao.listarTodas();
        for (Concepto e : lista) {
            if (filtro.isEmpty() || e.getDescripcion().toLowerCase().contains(filtro.toLowerCase())) {
                modelo.addRow(new Object[]{e.getId(), e.getDescripcion(), e.getEstado()});
            }
        }
        vistaConcepto.tblConcepto.setModel(modelo);
    }

    private void agregar() {
        try {
            int id = Integer.parseInt(vistaConcepto.txtIdConcepto.getText().trim());
            String descripcion = vistaConcepto.txtDescripcionConcepto.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaConcepto, "La descripción no puede estar vacía.");
                return;
            }

            Concepto e = new Concepto();
            e.setId(id);
            e.setDescripcion(descripcion);
            e.setEstado("Activo");

            if (dao.agregar(e)) {
                JOptionPane.showMessageDialog(vistaConcepto, "Especie registrada correctamente.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaConcepto, "Error: no se pudo registrar (ID o descripción duplicada).");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaConcepto, "El ID debe ser numérico.");
        }
    }

    private void actualizar() {
        try {
            int id = Integer.parseInt(vistaConcepto.txtIdConcepto.getText().trim());
            String descripcion = vistaConcepto.txtDescripcionConcepto.getText().trim();
            String estado = vistaConcepto.txtEstadoConcepto.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaConcepto, "La descripción no puede estar vacía.");
                return;
            }

            Concepto e = new Concepto(id, descripcion, estado);
            if (dao.actualizar(e)) {
                JOptionPane.showMessageDialog(vistaConcepto, "Concepto actualizado.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaConcepto, "Error al actualizar.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaConcepto, "El ID debe ser numérico.");
        }
    }

    private void cambiarEstado() {
        int fila = vistaConcepto.tblConcepto.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaConcepto, "Seleccione un concepto primero.");
            return;
        }
        int id = (int) vistaConcepto.tblConcepto.getValueAt(fila, 0);
        String estadoActual = vistaConcepto.tblConcepto.getValueAt(fila, 2).toString();
        String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Anulado" : "Activo";

        if (dao.cambiarEstado(id, nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaConcepto, "Estado cambiado a " + nuevoEstado);
            cargarTabla("");
            limpiar();
        } else {
            JOptionPane.showMessageDialog(vistaConcepto, "No se pudo cambiar el estado.");
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaConcepto.txtIdConcepto.setText(String.valueOf(siguienteId));
        vistaConcepto.txtIdConcepto.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaConcepto.txtDescripcionConcepto.setText("");
        vistaConcepto.txtEstadoConcepto.setText("Activo");
        inicializarConsecutivoId();
    }

    // ===================== REGRESAR AL MENÚ =====================
    private void regresar() {
        vistaConcepto.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
