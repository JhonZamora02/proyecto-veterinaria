/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.TercerosDAO;
import View.frmTerceros;
import Model.Terceros;
import View.frmMenu;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CtrlTerceros implements ActionListener {

    private frmTerceros vistaTerceros;
    private TercerosDAO daoTerceros;
    private Terceros tercerosActual;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlTerceros(frmTerceros vistaTerceros, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaTerceros = vistaTerceros;
        this.vistaMenuAnterior = menuAnterior;
        this.daoTerceros = new TercerosDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaTerceros.setLocationRelativeTo(null);
        vistaTerceros.setResizable(false);
        vistaTerceros.setSize(1144, 953);

        this.vistaTerceros.btnAgregarTercero.addActionListener(this);
        this.vistaTerceros.btnActualizarTercero.addActionListener(this);
        this.vistaTerceros.btnLimpiarTercero.addActionListener(this);
        this.vistaTerceros.btnAnularTercero.addActionListener(this);
        this.vistaTerceros.btnBuscar.addActionListener(this);
        this.vistaTerceros.btnRegresa.addActionListener(this);

        this.vistaTerceros.tblTercero.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                cargarDesdeTabla();
            }
        });

        cargarTabla();
        limpiarCampos();
        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaTerceros.btnAgregarTercero) {
            agregarTerceros();
        }
        if (e.getSource() == vistaTerceros.btnActualizarTercero) {
            actualizarTerceros();
        }
        if (e.getSource() == vistaTerceros.btnAnularTercero) {
            cambiarEstado();
        }
        if (e.getSource() == vistaTerceros.btnLimpiarTercero) {
            limpiarCampos();
        }
        if (e.getSource() == vistaTerceros.btnBuscar) {
            buscar();
        }
        if (e.getSource() == vistaTerceros.btnRegresa) {
            regresar();
        }
    }

    // ===================== MÉTODOS =====================
    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vistaTerceros.tblTercero.getModel();
        modelo.setRowCount(0);
        List<Object[]> lista = daoTerceros.listarTodos();
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = table.getValueAt(row, 9).toString();
                if ("Anulado".equalsIgnoreCase(estado)) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else if ("Activo".equalsIgnoreCase(estado)) {
                    c.setBackground(Color.GREEN);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        if (vistaTerceros.tblTercero.getColumnCount() > 9) {
            vistaTerceros.tblTercero.getColumnModel().getColumn(9).setCellRenderer(renderer);
        }
    }

    private void agregarTerceros() {
        try {
            int id = Integer.parseInt(vistaTerceros.txtIdTercero.getText());
            String descripcion = vistaTerceros.txtDescTercero.getText();
            String identificacion = vistaTerceros.txtIdenTercero.getText();
            String representantelegal = vistaTerceros.txtRepresTercero.getText();
            String razonsocial = vistaTerceros.txtRazonTercero.getText();
            String direccion = vistaTerceros.txtDirecTercero.getText();
            String telefono1 = vistaTerceros.txtTel1Tercero.getText();
            String telefono2 = vistaTerceros.txtTel2Tercero.getText();
            String observaciones = vistaTerceros.txtObsTercero.getText();

            if (daoTerceros.existeIdentificacion(identificacion)) {
                JOptionPane.showMessageDialog(vistaTerceros, "Ya existe un tercero con esa identificación.");
                return;
            }

            Terceros t = new Terceros(id, descripcion, identificacion, representantelegal, razonsocial, direccion, telefono1, telefono2, observaciones);
            t.setUsucrea(usuarioLogueado);

            if (daoTerceros.agregar(t)) {
                JOptionPane.showMessageDialog(vistaTerceros, "Tercero registrado correctamente.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vistaTerceros, "Error al registrar al tercero.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaTerceros, "ID inválido.");
        }
    }

    private void actualizarTerceros() {
        if (this.tercerosActual == null) {
            JOptionPane.showMessageDialog(vistaTerceros, "Debe seleccionar un tercero para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.tercerosActual.setDescripcion(this.vistaTerceros.txtDescTercero.getText());
        this.tercerosActual.setIdentificacion(this.vistaTerceros.txtIdenTercero.getText());
        this.tercerosActual.setRepresentantelegal(this.vistaTerceros.txtRepresTercero.getText());
        this.tercerosActual.setRazonsocial(this.vistaTerceros.txtRazonTercero.getText());
        this.tercerosActual.setDireccion(this.vistaTerceros.txtDirecTercero.getText());
        this.tercerosActual.setTelefono1(this.vistaTerceros.txtTel1Tercero.getText());
        this.tercerosActual.setTelefono2(this.vistaTerceros.txtTel2Tercero.getText());
        this.tercerosActual.setObservaciones(this.vistaTerceros.txtObsTercero.getText());
        this.tercerosActual.setEstado(this.vistaTerceros.txtEstTercero.getText());

        if (this.daoTerceros.modificarTercero(this.tercerosActual)) {
            JOptionPane.showMessageDialog(vistaTerceros, "Tercero modificado con éxito.");
            limpiarCampos();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vistaTerceros, "Error al modificar el tercero.");
        }
    }

    private void buscar() {
        String identificacion = vistaTerceros.txtIdenTercero.getText().trim();
        if (identificacion.isEmpty()) {
            JOptionPane.showMessageDialog(vistaTerceros, "Ingrese una identificación para buscar.");
            return;
        }

        this.tercerosActual = daoTerceros.buscaridentificacion(identificacion);

        if (this.tercerosActual != null) {
            vistaTerceros.txtIdTercero.setText(String.valueOf(tercerosActual.getId()));
            vistaTerceros.txtDescTercero.setText(tercerosActual.getDescripcion());
            vistaTerceros.txtIdenTercero.setText(tercerosActual.getIdentificacion());
            vistaTerceros.txtRepresTercero.setText(tercerosActual.getRepresentantelegal());
            vistaTerceros.txtRazonTercero.setText(tercerosActual.getRazonsocial());
            vistaTerceros.txtDirecTercero.setText(tercerosActual.getDireccion());
            vistaTerceros.txtTel1Tercero.setText(tercerosActual.getTelefono1());
            vistaTerceros.txtTel2Tercero.setText(tercerosActual.getTelefono2());
            vistaTerceros.txtObsTercero.setText(tercerosActual.getObservaciones());
            vistaTerceros.txtEstTercero.setText(tercerosActual.getEstado());
            JOptionPane.showMessageDialog(vistaTerceros, "Tercero encontrado.");
        } else {
            JOptionPane.showMessageDialog(vistaTerceros, "No encontrado.");
        }
    }

    private void cambiarEstado() {
        if (this.tercerosActual == null) {
            JOptionPane.showMessageDialog(vistaTerceros, "Debe seleccionar un tercero para anular/reactivar.");
            return;
        }

        String identificacion = this.tercerosActual.getIdentificacion();
        boolean anular = this.tercerosActual.getEstado().equalsIgnoreCase("Activo");

        boolean ok = daoTerceros.cambiarEstado(identificacion, usuarioLogueado, anular);
        if (ok) {
            this.tercerosActual = daoTerceros.buscaridentificacion(identificacion);
            String nuevoEstado = (this.tercerosActual != null ? this.tercerosActual.getEstado() : "Desconocido");
            JOptionPane.showMessageDialog(vistaTerceros, "Operación exitosa. Estado actual: " + nuevoEstado);
            limpiarCampos();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vistaTerceros, "Error al cambiar el estado.");
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = daoTerceros.obtenerSiguienteId();
        vistaTerceros.txtIdTercero.setText(String.valueOf(siguienteId));
        vistaTerceros.txtIdTercero.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiarCampos() {
        vistaTerceros.txtDescTercero.setText("");
        vistaTerceros.txtIdenTercero.setText("");
        vistaTerceros.txtRepresTercero.setText("");
        vistaTerceros.txtRazonTercero.setText("");
        vistaTerceros.txtDirecTercero.setText("");
        vistaTerceros.txtTel1Tercero.setText("");
        vistaTerceros.txtTel2Tercero.setText("");
        vistaTerceros.txtObsTercero.setText("");
        vistaTerceros.txtEstTercero.setText("Activo");
        this.tercerosActual = null;
        inicializarConsecutivoId();
    }

    private void cargarDesdeTabla() {
        int fila = vistaTerceros.tblTercero.getSelectedRow();
        if (fila != -1) {
            String identificacion = vistaTerceros.tblTercero.getValueAt(fila, 2).toString();
            this.tercerosActual = this.daoTerceros.buscaridentificacion(identificacion);

            if (this.tercerosActual != null) {
                vistaTerceros.txtIdTercero.setText(String.valueOf(this.tercerosActual.getId()));
                vistaTerceros.txtDescTercero.setText(this.tercerosActual.getDescripcion());
                vistaTerceros.txtIdenTercero.setText(this.tercerosActual.getIdentificacion());
                vistaTerceros.txtRepresTercero.setText(this.tercerosActual.getRepresentantelegal());
                vistaTerceros.txtRazonTercero.setText(this.tercerosActual.getRazonsocial());
                vistaTerceros.txtDirecTercero.setText(this.tercerosActual.getDireccion());
                vistaTerceros.txtTel1Tercero.setText(this.tercerosActual.getTelefono1());
                vistaTerceros.txtTel2Tercero.setText(this.tercerosActual.getTelefono2());
                vistaTerceros.txtObsTercero.setText(this.tercerosActual.getObservaciones());
                vistaTerceros.txtEstTercero.setText(this.tercerosActual.getEstado());
            }
        }
    }

    private void regresar() {
        vistaTerceros.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
