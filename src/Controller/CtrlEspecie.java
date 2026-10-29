/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.EspecieDAO;
import Model.Especie;
import View.frmEspecie;
import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CtrlEspecie implements ActionListener {

    private frmEspecie vistaEspecie;
    private EspecieDAO dao;
    private frmMenu vistaMenuAnterior;
    private String usuarioLogueado;

    public CtrlEspecie(frmEspecie vistaEspecie, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaEspecie = vistaEspecie;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new EspecieDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaEspecie.setLocationRelativeTo(null);
        vistaEspecie.setResizable(false);
        vistaEspecie.setSize(983, 640);

        // eventos
        this.vistaEspecie.btnAgregarEspecie.addActionListener(this);
        this.vistaEspecie.btnActualizarEspecie.addActionListener(this);
        this.vistaEspecie.btnEstadoEspecie.addActionListener(this);
        this.vistaEspecie.btnEliminarEspecie.addActionListener(this); // 👈 nuevo
        this.vistaEspecie.btnLimpiarEspecie.addActionListener(this);
        this.vistaEspecie.btnRegresa.addActionListener(this);

        // cargar tabla inicial
        cargarTabla("");

        this.vistaEspecie.txtEstadoEspecie.setText("Activo");
        this.vistaEspecie.txtEstadoEspecie.setEnabled(false);

        // evento de selección en la tabla
        this.vistaEspecie.tblEspecie.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = vistaEspecie.tblEspecie.getSelectedRow();
                if (fila != -1) {
                    vistaEspecie.txtIdEspecie.setText(vistaEspecie.tblEspecie.getValueAt(fila, 0).toString());
                    vistaEspecie.txtDescripcionEspecie.setText(vistaEspecie.tblEspecie.getValueAt(fila, 1).toString());
                    vistaEspecie.txtEstadoEspecie.setText(vistaEspecie.tblEspecie.getValueAt(fila, 2).toString());
                }
            }
        });

        // evento de búsqueda en el txt
        this.vistaEspecie.txtBuscadorEspecie.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String filtro = vistaEspecie.txtBuscadorEspecie.getText().trim();
                cargarTabla(filtro);
            }
        });

        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaEspecie.btnAgregarEspecie) {
            agregar();
        }
        if (e.getSource() == vistaEspecie.btnActualizarEspecie) {
            actualizar();
        }
        if (e.getSource() == vistaEspecie.btnEstadoEspecie) {
            cambiarEstado();
        }
        if (e.getSource() == vistaEspecie.btnEliminarEspecie) {
            eliminar();
        }
        if (e.getSource() == vistaEspecie.btnLimpiarEspecie) {
            limpiar();
        }
        if (e.getSource() == vistaEspecie.btnRegresa) {
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

        List<Especie> lista = dao.listarTodas();
        for (Especie e : lista) {
            if (filtro.isEmpty() || e.getDescripcion().toLowerCase().contains(filtro.toLowerCase())) {
                modelo.addRow(new Object[]{e.getId(), e.getDescripcion(), e.getEstado()});
            }
        }
        vistaEspecie.tblEspecie.setModel(modelo);
    }

    private void agregar() {
        try {
            int id = Integer.parseInt(vistaEspecie.txtIdEspecie.getText().trim());
            String descripcion = vistaEspecie.txtDescripcionEspecie.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaEspecie, "La descripción no puede estar vacía.");
                return;
            }

            Especie e = new Especie();
            e.setId(id);
            e.setDescripcion(descripcion);
            e.setEstado("Activo");

            if (dao.agregar(e)) {
                JOptionPane.showMessageDialog(vistaEspecie, "Especie registrada correctamente.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaEspecie, "Error: no se pudo registrar (ID o descripción duplicada).");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaEspecie, "El ID debe ser numérico.");
        }
    }

    private void actualizar() {
        try {
            int id = Integer.parseInt(vistaEspecie.txtIdEspecie.getText().trim());
            String descripcion = vistaEspecie.txtDescripcionEspecie.getText().trim();
            String estado = vistaEspecie.txtEstadoEspecie.getText().trim();

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaEspecie, "La descripción no puede estar vacía.");
                return;
            }

            Especie e = new Especie(id, descripcion, estado);
            if (dao.actualizar(e)) {
                JOptionPane.showMessageDialog(vistaEspecie, "Especie actualizada.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaEspecie, "Error al actualizar.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaEspecie, "El ID debe ser numérico.");
        }
    }

    private void cambiarEstado() {
        int fila = vistaEspecie.tblEspecie.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaEspecie, "Seleccione una especie primero.");
            return;
        }
        int id = (int) vistaEspecie.tblEspecie.getValueAt(fila, 0);
        String estadoActual = vistaEspecie.tblEspecie.getValueAt(fila, 2).toString();
        String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Inactivo" : "Activo";

        if (dao.cambiarEstado(id, nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaEspecie, "Estado cambiado a " + nuevoEstado);
            cargarTabla("");
            limpiar();
        } else {
            JOptionPane.showMessageDialog(vistaEspecie, "No se pudo cambiar el estado.");
        }
    }

    private void eliminar() {
        int fila = vistaEspecie.tblEspecie.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaEspecie, "Seleccione una especie primero.");
            return;
        }
        int id = (int) vistaEspecie.tblEspecie.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(vistaEspecie,
                "¿Está seguro de que desea eliminar esta especie?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminar(id)) {
                JOptionPane.showMessageDialog(vistaEspecie, "Especie eliminada o marcada como inactiva según dependencias.");
                cargarTabla("");
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaEspecie, "No se pudo eliminar la especie.");
            }
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaEspecie.txtIdEspecie.setText(String.valueOf(siguienteId));
        vistaEspecie.txtIdEspecie.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaEspecie.txtDescripcionEspecie.setText("");
        vistaEspecie.txtEstadoEspecie.setText("Activo");
        vistaEspecie.txtBuscadorEspecie.setText("");
        inicializarConsecutivoId();
    }

    // ===================== REGRESAR AL MENÚ =====================
    private void regresar() {
        vistaEspecie.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
