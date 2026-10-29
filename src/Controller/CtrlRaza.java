/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.RazaDAO;
import DAO.EspecieDAO;
import Model.Raza;
import Model.Especie;
import View.frmRaza;
import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CtrlRaza implements ActionListener {

    private frmRaza vistaRaza;
    private RazaDAO dao;
    private EspecieDAO especieDAO;
    private Raza razaActual;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlRaza(frmRaza vistaRaza, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaRaza = vistaRaza;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new RazaDAO();
        this.especieDAO = new EspecieDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaRaza.setLocationRelativeTo(null);
        vistaRaza.setResizable(false);
        vistaRaza.setSize(986, 714);

        // Eventos botones
        this.vistaRaza.btnAgregarRaza.addActionListener(this);
        this.vistaRaza.btnActualizarRaza.addActionListener(this);
        this.vistaRaza.btnEstadoRaza.addActionListener(this);
        this.vistaRaza.btnEliminarRaza.addActionListener(this);
        this.vistaRaza.btnLimpiarRaza.addActionListener(this);
        this.vistaRaza.btnRegresa.addActionListener(this);

        this.vistaRaza.txtEstadoRaza.setText("Activo");
        this.vistaRaza.txtEstadoRaza.setEnabled(false);

        this.vistaRaza.txtBuscadorRaza.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscar();
            }
        });

        // Evento tabla (cargar datos en los campos)
        this.vistaRaza.tblRaza.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDesdeTabla();
            }
        });

        // Cargar especies en combo
        cargarComboEspecies();

        // Cargar razas en tabla
        cargarTabla();

        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaRaza.btnAgregarRaza) {
            agregar();
        }
        if (e.getSource() == vistaRaza.btnActualizarRaza) {
            actualizar();
        }
        if (e.getSource() == vistaRaza.btnEstadoRaza) {
            cambiarEstado();
        }
        if (e.getSource() == vistaRaza.btnEliminarRaza) {
            eliminar();
        }
        if (e.getSource() == vistaRaza.btnLimpiarRaza) {
            limpiar();
        }
        if (e.getSource() == vistaRaza.btnRegresa) {
            regresar();
        }
    }

    // ==================== MÉTODOS ====================
    private void cargarComboEspecies() {
        vistaRaza.cbxEspecieRaza.removeAllItems();
        List<Especie> especies = especieDAO.listarActivas();
        for (Especie esp : especies) {
            vistaRaza.cbxEspecieRaza.addItem(esp);
        }
    }

    private void cargarTabla() {
        String[] cols = {"ID", "Descripción", "Estado", "Especie"};
        DefaultTableModel modelo = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<Object[]> lista = dao.listarTodos();
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
        vistaRaza.tblRaza.setModel(modelo);
    }

    private void agregar() {
        try {
            int id = Integer.parseInt(vistaRaza.txtIdRaza.getText());
            String descripcion = vistaRaza.txtDescripcionRaza.getText().trim();
            Especie especie = (Especie) vistaRaza.cbxEspecieRaza.getSelectedItem();

            if (descripcion.isEmpty() || especie == null) {
                JOptionPane.showMessageDialog(vistaRaza, "Debe llenar todos los campos.");
                return;
            }

            Raza r = new Raza();
            r.setId(id);
            r.setDescripcion(descripcion);
            r.setEstado("Activo");
            r.setEspecieId(especie.getId());

            if (dao.agregar(r)) {
                JOptionPane.showMessageDialog(vistaRaza, "Raza agregada correctamente.");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaRaza, "Error al agregar raza.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaRaza, "El ID debe ser un número entero.");
        }
    }

    private void actualizar() {
        if (razaActual == null) {
            JOptionPane.showMessageDialog(vistaRaza, "Debe seleccionar una raza de la tabla.");
            return;
        }

        try {
            razaActual.setDescripcion(vistaRaza.txtDescripcionRaza.getText().trim());
            razaActual.setEstado(vistaRaza.txtEstadoRaza.getText().trim());
            Especie especie = (Especie) vistaRaza.cbxEspecieRaza.getSelectedItem();
            if (especie != null) {
                razaActual.setEspecieId(especie.getId());
            }

            if (dao.actualizar(razaActual)) {
                JOptionPane.showMessageDialog(vistaRaza, "Raza actualizada correctamente.");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaRaza, "Error al actualizar raza.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vistaRaza, "Error: " + ex.getMessage());
        }
    }

    private void cambiarEstado() {
        if (razaActual == null) {
            JOptionPane.showMessageDialog(vistaRaza, "Debe seleccionar una raza.");
            return;
        }
        String nuevoEstado = razaActual.getEstado().equalsIgnoreCase("Activo") ? "Inactivo" : "Activo";
        if (dao.cambiarEstado(razaActual.getId(), nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaRaza, "Estado cambiado a: " + nuevoEstado);
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(vistaRaza, "Error al cambiar estado.");
        }
    }

    private void eliminar() {
        if (razaActual == null) {
            JOptionPane.showMessageDialog(vistaRaza, "Debe seleccionar una raza para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                vistaRaza,
                "¿Está seguro de eliminar la raza seleccionada?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Intentar eliminar desde el DAO
            if (dao.eliminar(razaActual.getId())) {
                JOptionPane.showMessageDialog(vistaRaza, "Raza eliminada correctamente.");
                cargarTabla();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vistaRaza, "No se pudo eliminar. Puede que tenga mascotas asociadas, se marcó como Inactiva.");
                cargarTabla();
                limpiar();
            }
        }
    }

    private void buscar() {
        String filtro = vistaRaza.txtBuscadorRaza.getText().trim();
        String[] cols = {"ID", "Descripción", "Estado", "Especie"};
        DefaultTableModel modelo = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<Object[]> lista = dao.buscarPorDescripcion(filtro);
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
        vistaRaza.tblRaza.setModel(modelo);
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaRaza.txtIdRaza.setText(String.valueOf(siguienteId));
        vistaRaza.txtIdRaza.setEnabled(false);
    }

    private void limpiar() {
        vistaRaza.txtDescripcionRaza.setText("");
        vistaRaza.txtEstadoRaza.setText("Activo");
        if (vistaRaza.cbxEspecieRaza.getItemCount() > 0) {
            vistaRaza.cbxEspecieRaza.setSelectedIndex(0);
        }
        razaActual = null;
        inicializarConsecutivoId();
    }

    private void cargarDesdeTabla() {
        int fila = vistaRaza.tblRaza.getSelectedRow();
        if (fila != -1) {
            razaActual = new Raza();
            razaActual.setId((int) vistaRaza.tblRaza.getValueAt(fila, 0));
            razaActual.setDescripcion(vistaRaza.tblRaza.getValueAt(fila, 1).toString());
            razaActual.setEstado(vistaRaza.tblRaza.getValueAt(fila, 2).toString());

            vistaRaza.txtIdRaza.setText(String.valueOf(razaActual.getId()));
            vistaRaza.txtDescripcionRaza.setText(razaActual.getDescripcion());
            vistaRaza.txtEstadoRaza.setText(razaActual.getEstado());

            // Buscar y seleccionar especie en el combo
            String especieNombre = vistaRaza.tblRaza.getValueAt(fila, 3).toString();
            for (int i = 0; i < vistaRaza.cbxEspecieRaza.getItemCount(); i++) {
                Especie esp = (Especie) vistaRaza.cbxEspecieRaza.getItemAt(i);
                if (esp.getDescripcion().equalsIgnoreCase(especieNombre)) {
                    vistaRaza.cbxEspecieRaza.setSelectedIndex(i);
                    razaActual.setEspecieId(esp.getId());
                    break;
                }
            }
        }
    }

    // ===================== REGRESAR AL MENÚ =====================
    private void regresar() {
        vistaRaza.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
