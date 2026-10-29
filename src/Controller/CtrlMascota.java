/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.MascotaDAO;
import DAO.RazaDAO;
import Model.Mascota;
import Model.Raza;
import View.frmMascota;
import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CtrlMascota implements ActionListener {

    private frmMascota vistaMascota;
    private MascotaDAO daoMascota;
    private RazaDAO daoRaza;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlMascota(frmMascota vistaMascota, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaMascota = vistaMascota;
        this.vistaMenuAnterior = menuAnterior;
        this.usuarioLogueado = usuarioLogueado;
        this.daoMascota = new MascotaDAO();
        this.daoRaza = new RazaDAO();

        vistaMascota.setLocationRelativeTo(null);
        vistaMascota.setResizable(false);
        vistaMascota.setSize(928, 830);

        // Eventos botones
        this.vistaMascota.btnAgregarMas.addActionListener(this);
        this.vistaMascota.btnActualizarMas.addActionListener(this);
        this.vistaMascota.btnEliminarMas.addActionListener(this);
        this.vistaMascota.btnLimpiarMas.addActionListener(this);
        this.vistaMascota.btnRegresa.addActionListener(this);

        // Combos de sexo y estado
        this.vistaMascota.cbxSexoMas.removeAllItems();
        this.vistaMascota.cbxSexoMas.addItem("Macho");
        this.vistaMascota.cbxSexoMas.addItem("Hembra");

        this.vistaMascota.cbxEstadoMas.removeAllItems();
        this.vistaMascota.cbxEstadoMas.addItem("Activo");
        this.vistaMascota.cbxEstadoMas.addItem("Inactivo");
        this.vistaMascota.cbxEstadoMas.addItem("Fallecido");
        this.vistaMascota.cbxEstadoMas.setSelectedItem("Activo");

        // Buscador
        this.vistaMascota.txtBuscadorMas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String filtro = vistaMascota.txtBuscadorMas.getText().trim();
                cargarTabla(filtro);
            }
        });

        // Evento tabla
        this.vistaMascota.tblMascotas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDesdeTabla();
            }
        });

        // Cargar combos y tabla inicial
        cargarComboRaza();
        cargarTabla("");
        inicializarConsecutivoId();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaMascota.btnAgregarMas) {
            agregarMascota();
        }
        if (e.getSource() == vistaMascota.btnActualizarMas) {
            actualizarMascota();
        }
        if (e.getSource() == vistaMascota.btnEliminarMas) {
            eliminarMascota();
        }
        if (e.getSource() == vistaMascota.btnLimpiarMas) {
            limpiarCampos();
        }
        if (e.getSource() == vistaMascota.btnRegresa) {
            regresar();
        }
    }

    // ================= MÉTODOS =================
    private void cargarComboRaza() {
        vistaMascota.cbxRazaMas.removeAllItems();
        for (Raza r : daoRaza.listarActivas()) {
            vistaMascota.cbxRazaMas.addItem(r);
        }
    }

    private void cargarTabla(String filtro) {
        String[] cols = {"ID", "Nombre", "Edad", "Propietario", "Sexo", "Raza", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<Mascota> lista = (filtro == null || filtro.isEmpty())
                ? daoMascota.listarTodas()
                : daoMascota.buscar(filtro);

        for (Mascota m : lista) {
            modelo.addRow(new Object[]{
                m.getId(),
                m.getNombre(),
                m.getEdad(),
                m.getPropietario(),
                m.getSexo(),
                m.getRazaDescripcion(),
                m.getEstado()
            });
        }
        vistaMascota.tblMascotas.setModel(modelo);
    }

    private void cargarDesdeTabla() {
        int fila = vistaMascota.tblMascotas.getSelectedRow();
        if (fila != -1) {
            vistaMascota.txtIdMas.setText(vistaMascota.tblMascotas.getValueAt(fila, 0).toString());
            vistaMascota.txtNombreMas.setText(vistaMascota.tblMascotas.getValueAt(fila, 1).toString());
            vistaMascota.txtEdadMas.setText(vistaMascota.tblMascotas.getValueAt(fila, 2).toString());
            vistaMascota.txtPropietarioMas.setText(vistaMascota.tblMascotas.getValueAt(fila, 3).toString());
            vistaMascota.cbxSexoMas.setSelectedItem(vistaMascota.tblMascotas.getValueAt(fila, 4).toString());

            String razaNombre = vistaMascota.tblMascotas.getValueAt(fila, 5).toString();
            for (int i = 0; i < vistaMascota.cbxRazaMas.getItemCount(); i++) {
                if (vistaMascota.cbxRazaMas.getItemAt(i).toString().equals(razaNombre)) {
                    vistaMascota.cbxRazaMas.setSelectedIndex(i);
                    break;
                }
            }

            vistaMascota.cbxEstadoMas.setSelectedItem(vistaMascota.tblMascotas.getValueAt(fila, 6).toString());
        }
    }

    private void agregarMascota() {
        try {
            int id = Integer.parseInt(vistaMascota.txtIdMas.getText().trim());
            String nombre = vistaMascota.txtNombreMas.getText().trim();
            int edad = Integer.parseInt(vistaMascota.txtEdadMas.getText().trim());
            String propietario = vistaMascota.txtPropietarioMas.getText().trim();
            String sexo = vistaMascota.cbxSexoMas.getSelectedItem().toString();
            Raza raza = (Raza) vistaMascota.cbxRazaMas.getSelectedItem();

            if (nombre.isEmpty() || propietario.isEmpty() || raza == null) {
                JOptionPane.showMessageDialog(vistaMascota, "Debe llenar todos los campos obligatorios.");
                return;
            }
            if (edad <= 0) {
                JOptionPane.showMessageDialog(vistaMascota, "La edad debe ser un número positivo.");
                return;
            }

            // Estado siempre por defecto en "Activo"
            String estado = "Activo";

            // Verificar duplicado
            if (daoMascota.existeMascota(id)) {
                JOptionPane.showMessageDialog(vistaMascota, "El ID ya está registrado.");
                return;
            }

            Mascota m = new Mascota(id, nombre, edad, propietario, sexo, raza.getId(), estado);
            if (daoMascota.agregar(m)) {
                JOptionPane.showMessageDialog(vistaMascota, "Mascota registrada correctamente.");
                cargarTabla("");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vistaMascota, "Error al registrar la mascota.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaMascota, "ID y Edad deben ser valores numéricos.");
        }
    }

    private void actualizarMascota() {
        try {
            int id = Integer.parseInt(vistaMascota.txtIdMas.getText().trim());
            String nombre = vistaMascota.txtNombreMas.getText().trim();
            int edad = Integer.parseInt(vistaMascota.txtEdadMas.getText().trim());
            String propietario = vistaMascota.txtPropietarioMas.getText().trim();
            String sexo = vistaMascota.cbxSexoMas.getSelectedItem().toString();
            Raza raza = (Raza) vistaMascota.cbxRazaMas.getSelectedItem();
            String estado = vistaMascota.cbxEstadoMas.getSelectedItem().toString();

            if (nombre.isEmpty() || propietario.isEmpty() || raza == null) {
                JOptionPane.showMessageDialog(vistaMascota, "Debe llenar todos los campos obligatorios.");
                return;
            }

            Mascota m = new Mascota(id, nombre, edad, propietario, sexo, raza.getId(), estado);
            if (daoMascota.actualizar(m)) {
                JOptionPane.showMessageDialog(vistaMascota, "Mascota actualizada correctamente.");
                cargarTabla("");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vistaMascota, "Error al actualizar mascota.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaMascota, "ID y Edad deben ser valores numéricos.");
        }
    }

    private void eliminarMascota() {
        int fila = vistaMascota.tblMascotas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaMascota, "Seleccione una mascota primero.");
            return;
        }
        int id = (int) vistaMascota.tblMascotas.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(vistaMascota,
                "¿Está seguro de eliminar esta mascota?",
                "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (daoMascota.eliminar(id)) {
                JOptionPane.showMessageDialog(vistaMascota, "Mascota eliminada (marcada como Inactiva).");
                cargarTabla("");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vistaMascota, "Error al eliminar mascota.");
            }
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = daoMascota.obtenerSiguienteId();
        vistaMascota.txtIdMas.setText(String.valueOf(siguienteId));
        vistaMascota.txtIdMas.setEnabled(false);
    }

    private void limpiarCampos() {
        vistaMascota.txtNombreMas.setText("");
        vistaMascota.txtEdadMas.setText("");
        vistaMascota.txtPropietarioMas.setText("");
        vistaMascota.cbxSexoMas.setSelectedIndex(0);
        vistaMascota.cbxRazaMas.setSelectedIndex(-1);
        vistaMascota.cbxEstadoMas.setSelectedItem("Activo");
        vistaMascota.txtBuscadorMas.setText("");
        inicializarConsecutivoId();
    }

    private void regresar() {
        vistaMascota.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
