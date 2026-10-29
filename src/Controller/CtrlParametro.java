/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.ParametroDAO;
import Model.Parametro;
import View.frmMenu;
import View.frmParametro;
import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CtrlParametro implements ActionListener {

    private frmParametro vistaParametro;
    private ParametroDAO dao;
    private Parametro parametroActual;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlParametro(frmParametro vistaParametro, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaParametro = vistaParametro;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new ParametroDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaParametro.setLocationRelativeTo(null);
        vistaParametro.setResizable(false);
        vistaParametro.setSize(1123, 776);

        // listeners
        this.vistaParametro.btnRegistrar.addActionListener(this);
        this.vistaParametro.btnModificar.addActionListener(this);
        this.vistaParametro.btnAnular.addActionListener(this);
        this.vistaParametro.btnRegresa.addActionListener(this);

        this.vistaParametro.txtEstado.setText("Activo");
        this.vistaParametro.txtEstado.setEnabled(false);

        this.vistaParametro.tblParametro.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cargarDesdeTabla();
            }
        });

        cargarTabla();
        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaParametro.btnRegistrar) {
            registrar();
        }
        if (e.getSource() == vistaParametro.btnModificar) {
            modificar();
        }
        if (e.getSource() == vistaParametro.btnAnular) {
            cambiarEstado();
        }
        if (e.getSource() == vistaParametro.btnRegresa) {
            regresar();
        }
    }

    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vistaParametro.tblParametro.getModel();
        modelo.setRowCount(0);
        List<Object[]> lista = dao.listarTodos();
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }

    private void registrar() {
        try {
            String id = vistaParametro.txtId.getText().trim();
            String descripcion = vistaParametro.txtDescripcion.getText().trim();
            String unidad = vistaParametro.txtUnidad.getText().trim();
            String formula = vistaParametro.txtFormula.getText().trim();
            String referencia1 = vistaParametro.txtReferencia1.getText().trim();
            String referencia2 = vistaParametro.txtReferencia2.getText().trim();

            Parametro p = new Parametro(id, descripcion, unidad, "Activo", formula, referencia1, referencia2);
            p.setUsu_crea(usuarioLogueado);

            if (dao.insertarParametro(p)) {
                JOptionPane.showMessageDialog(vistaParametro, "Parametro registrado.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(vistaParametro, "Error al registrar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vistaParametro, "Error al registrar: " + ex.getMessage());
        }
    }

    private void modificar() {
        if (this.parametroActual == null) {
            JOptionPane.showMessageDialog(vistaParametro, "Debe seleccionar un parámetro para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.parametroActual.setPara_descripcion(this.vistaParametro.txtDescripcion.getText());
        this.parametroActual.setPara_unidad(this.vistaParametro.txtUnidad.getText());
        this.parametroActual.setPara_formula(this.vistaParametro.txtFormula.getText());
        this.parametroActual.setPara_referencia1(this.vistaParametro.txtReferencia1.getText());
        this.parametroActual.setPara_referencia2(this.vistaParametro.txtReferencia2.getText());

        if (this.dao.actualizar(this.parametroActual)) {
            JOptionPane.showMessageDialog(vistaParametro, "Parametro modificado con éxito.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vistaParametro, "Error al modificar el parametro.");
        }
    }

    // Alterna Activo <-> Anulado (usa cambiarEstadoParametro del DAO)
    private void cambiarEstado() {
        if (this.parametroActual == null) {
            JOptionPane.showMessageDialog(vistaParametro, "Debe seleccionar un parámetro para cambiar estado.");
            return;
        }

        String id = this.parametroActual.getPara_id();
        boolean ok = dao.cambiarEstadoParametro(id, usuarioLogueado);

        if (ok) {
            JOptionPane.showMessageDialog(vistaParametro, "Estado cambiado correctamente.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vistaParametro, "Error al cambiar estado.");
        }
    }

    private void cargarDesdeTabla() {
        int fila = vistaParametro.tblParametro.getSelectedRow();
        if (fila != -1) {
            parametroActual = new Parametro();

            String id = vistaParametro.tblParametro.getValueAt(fila, 0).toString();
            String descripcion = vistaParametro.tblParametro.getValueAt(fila, 1).toString();
            String unidad = vistaParametro.tblParametro.getValueAt(fila, 2).toString();
            String estado = vistaParametro.tblParametro.getValueAt(fila, 3).toString();
            String formula = vistaParametro.tblParametro.getValueAt(fila, 4) == null ? "" : vistaParametro.tblParametro.getValueAt(fila, 4).toString();
            String referencia1 = vistaParametro.tblParametro.getValueAt(fila, 5) == null ? "" : vistaParametro.tblParametro.getValueAt(fila, 5).toString();
            String referencia2 = vistaParametro.tblParametro.getValueAt(fila, 6) == null ? "" : vistaParametro.tblParametro.getValueAt(fila, 6).toString();

            vistaParametro.txtId.setText(id);
            vistaParametro.txtDescripcion.setText(descripcion);
            vistaParametro.txtUnidad.setText(unidad);
            vistaParametro.txtEstado.setText(estado);
            vistaParametro.txtFormula.setText(formula);
            vistaParametro.txtReferencia1.setText(referencia1);
            vistaParametro.txtReferencia2.setText(referencia2);

            parametroActual.setPara_id(id);
            parametroActual.setPara_descripcion(descripcion);
            parametroActual.setPara_unidad(unidad);
            parametroActual.setPara_estado(estado);
            parametroActual.setPara_formula(formula);
            parametroActual.setPara_referencia1(referencia1);
            parametroActual.setPara_referencia2(referencia2);
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaParametro.txtId.setText(String.valueOf(siguienteId));
        vistaParametro.txtId.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaParametro.txtDescripcion.setText("");
        vistaParametro.txtUnidad.setText("");
        vistaParametro.txtEstado.setText("Activo");
        vistaParametro.txtFormula.setText("");
        vistaParametro.txtReferencia1.setText("");
        vistaParametro.txtReferencia2.setText("");
        parametroActual = null;
        inicializarConsecutivoId();
    }

    private void regresar() {
        vistaParametro.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
