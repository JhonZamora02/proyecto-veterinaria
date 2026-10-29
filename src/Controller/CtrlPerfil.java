/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.PerfilDAO;
import Model.Perfil;
import View.frmMenu;
import View.frmPerfil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CtrlPerfil implements ActionListener {

    private frmPerfil vistaPerfil;
    private PerfilDAO dao;
    private Perfil perfilActual;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlPerfil(frmPerfil vistaPerfil, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaPerfil = vistaPerfil;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new PerfilDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaPerfil.setLocationRelativeTo(null);
        vistaPerfil.setResizable(false);
        vistaPerfil.setSize(685, 645);

        // Eventos botones
        this.vistaPerfil.btnAgregar.addActionListener(this);
        this.vistaPerfil.btnActualizar.addActionListener(this);
        this.vistaPerfil.btnEstado.addActionListener(this);
        this.vistaPerfil.btnLimpiar.addActionListener(this);
        this.vistaPerfil.btnRegresa.addActionListener(this);

        this.vistaPerfil.txtEstado.setText("Activo");
        this.vistaPerfil.txtEstado.setEnabled(false);

        // Evento tabla (cargar datos en los campos)
        this.vistaPerfil.tblPerfil.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDesdeTabla();
            }
        });

        // Cargar perfiles en tabla
        cargarTabla();
        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaPerfil.btnAgregar) {
            agregar();
        }
        if (e.getSource() == vistaPerfil.btnActualizar) {
            actualizar();
        }
        if (e.getSource() == vistaPerfil.btnEstado) {
            cambiarEstado();
        }
        if (e.getSource() == vistaPerfil.btnLimpiar) {
            limpiar();
        }
        if (e.getSource() == vistaPerfil.btnRegresa) {
            regresar();
        }
    }

    // ==================== MÉTODOS ====================
    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vistaPerfil.tblPerfil.getModel();
        modelo.setRowCount(0);
        List<Object[]> lista = dao.listarTodos();
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }

    private void agregar() {
        try {
            String id = vistaPerfil.txtId.getText();
            String descripcion = vistaPerfil.txtDescripcion.getText();
            String estado = vistaPerfil.txtEstado.getText();

            Perfil p = new Perfil(id, descripcion, estado);

            if (dao.agregar(p)) {
                JOptionPane.showMessageDialog(vistaPerfil, "Perfil registrado.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(vistaPerfil, "Error al registrar.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaPerfil, "ID inválido.");
        }
    }

    private void actualizar() {

        String id = vistaPerfil.txtId.getText();
        String descripcion = vistaPerfil.txtDescripcion.getText();
        String estado = vistaPerfil.txtEstado.getText();

        Perfil p = new Perfil(id, descripcion, estado);

        if (this.dao.actualizar(p)) {
            JOptionPane.showMessageDialog(vistaPerfil, "Veterinario modificado con éxito.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vistaPerfil, "Error al modificar el veterinario.");
        }
    }

    private void cambiarEstado() {
        if (perfilActual == null) {
            JOptionPane.showMessageDialog(vistaPerfil, "Debe seleccionar una raza.");
            return;
        }
        String nuevoEstado = perfilActual.getPer_estado().equalsIgnoreCase("Activo") ? "Anulado" : "Activo";
        if (dao.cambiarEstado(perfilActual.getPer_id(), nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaPerfil, "Estado cambiado a: " + nuevoEstado);
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(vistaPerfil, "Error al cambiar estado.");
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaPerfil.txtId.setText(String.valueOf(siguienteId));
        vistaPerfil.txtId.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaPerfil.txtDescripcion.setText("");
        vistaPerfil.txtEstado.setText("Activo");
        inicializarConsecutivoId();
    }

    private void cargarDesdeTabla() {
        int fila = vistaPerfil.tblPerfil.getSelectedRow();
        if (fila != -1) {
            perfilActual = new Perfil();
            perfilActual.setPer_id(vistaPerfil.tblPerfil.getValueAt(fila, 0).toString());
            perfilActual.setPer_descripcion(vistaPerfil.tblPerfil.getValueAt(fila, 1).toString());
            perfilActual.setPer_estado(vistaPerfil.tblPerfil.getValueAt(fila, 2).toString());

            vistaPerfil.txtId.setText(String.valueOf(perfilActual.getPer_id()));
            vistaPerfil.txtDescripcion.setText(perfilActual.getPer_descripcion());
            vistaPerfil.txtEstado.setText(perfilActual.getPer_estado());
        }
    }

    // ===================== REGRESAR AL MENÚ =====================
    private void regresar() {
        vistaPerfil.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }

}
