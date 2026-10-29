/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.ExamenDAO;
import Model.Examen;
import View.frmExamen;
import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CtrlExamen implements ActionListener {

    private frmExamen vistaExamen;
    private ExamenDAO dao;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlExamen(frmExamen vistaExamen, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaExamen = vistaExamen;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new ExamenDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaExamen.setLocationRelativeTo(null);
        vistaExamen.setResizable(false);
        vistaExamen.setSize(874, 698);

        this.vistaExamen.btnAgregarExa.addActionListener(this);
        this.vistaExamen.btnActualizarExa.addActionListener(this);
        this.vistaExamen.btnAnularExa.addActionListener(this);
        this.vistaExamen.btnLimpiarExa.addActionListener(this);
        this.vistaExamen.btnRegresa.addActionListener(this);

        cargarTabla();

        // Evento: seleccionar fila de la tabla
        this.vistaExamen.tblExamenes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = vistaExamen.tblExamenes.getSelectedRow();
                if (fila != -1) {
                    vistaExamen.txtIdExa.setText(vistaExamen.tblExamenes.getValueAt(fila, 0).toString());
                    vistaExamen.txtDescripcionExa.setText(vistaExamen.tblExamenes.getValueAt(fila, 1).toString());
                    vistaExamen.txtValorExa.setText(vistaExamen.tblExamenes.getValueAt(fila, 2).toString());
                    vistaExamen.cmbTipoExa.setSelectedItem(vistaExamen.tblExamenes.getValueAt(fila, 3).toString());
                    vistaExamen.txtEstadoExa.setText(vistaExamen.tblExamenes.getValueAt(fila, 4).toString());
                }
            }
        });

        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaExamen.btnAgregarExa) {
            agregar();
        }
        if (e.getSource() == vistaExamen.btnActualizarExa) {
            actualizar();
        }
        if (e.getSource() == vistaExamen.btnAnularExa) {
            cambiarEstado();
        }
        if (e.getSource() == vistaExamen.btnLimpiarExa) {
            limpiar();
        }
        if (e.getSource() == vistaExamen.btnRegresa) {
            regresar();
        }
    }

    private void cargarTabla() {
        String[] cols = {"ID", "Descripción", "Valor", "Tipo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<Examen> lista = dao.listar();
        for (Examen e : lista) {
            modelo.addRow(new Object[]{
                e.getId(), e.getDescripcion(), e.getValor(), e.getTipo(), e.getEstado()
            });
        }
        vistaExamen.tblExamenes.setModel(modelo);
    }

    private void agregar() {
        try {
            String descripcion = vistaExamen.txtDescripcionExa.getText().trim();
            String tipo = vistaExamen.cmbTipoExa.getSelectedItem().toString();
            int valor = Integer.parseInt(vistaExamen.txtValorExa.getText().trim());

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaExamen, "Descripción no puede estar vacía.");
                return;
            }
            if (valor < 0) {
                JOptionPane.showMessageDialog(vistaExamen, "Valor debe ser mayor o igual a 0.");
                return;
            }

            Examen ex = new Examen();
            ex.setId(Integer.parseInt(vistaExamen.txtIdExa.getText()));
            ex.setDescripcion(descripcion);
            ex.setValor(valor);
            ex.setTipo(tipo);
            ex.setEstado("Activo");

            if (dao.agregar(ex)) {
                JOptionPane.showMessageDialog(vistaExamen, "Examen agregado correctamente.");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaExamen, "Error: Descripción ya existe o fallo en inserción.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaExamen, "Valor debe ser numérico (entero).");
        }
    }

    private void actualizar() {
        try {
            Examen ex = new Examen();
            ex.setId(Integer.parseInt(vistaExamen.txtIdExa.getText()));
            ex.setDescripcion(vistaExamen.txtDescripcionExa.getText().trim());
            ex.setValor(Integer.parseInt(vistaExamen.txtValorExa.getText().trim()));
            ex.setTipo(vistaExamen.cmbTipoExa.getSelectedItem().toString());
            ex.setEstado(vistaExamen.txtEstadoExa.getText());

            if (dao.actualizar(ex)) {
                JOptionPane.showMessageDialog(vistaExamen, "Examen actualizado.");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaExamen, "Error al actualizar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vistaExamen, "Error: " + ex.getMessage());
        }
    }

    private void cambiarEstado() {
        int fila = vistaExamen.tblExamenes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaExamen, "Seleccione un examen primero.");
            return;
        }
        int id = (int) vistaExamen.tblExamenes.getValueAt(fila, 0);
        String estadoActual = vistaExamen.tblExamenes.getValueAt(fila, 4).toString();
        String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Anulado" : "Activo";

        if (dao.cambiarEstado(id, nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaExamen, "Examen ahora está: " + nuevoEstado);
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(vistaExamen, "No se pudo cambiar el estado.");
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaExamen.txtIdExa.setText(String.valueOf(siguienteId));
        vistaExamen.txtIdExa.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaExamen.txtDescripcionExa.setText("");
        vistaExamen.txtValorExa.setText("");
        vistaExamen.cmbTipoExa.setSelectedIndex(0);
        vistaExamen.txtEstadoExa.setText("Activo");
        inicializarConsecutivoId();
    }

    // Método para regresar al menú
    private void regresar() {
        vistaExamen.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
