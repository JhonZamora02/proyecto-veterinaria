/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.VeterinarioDAO;
import Model.Veterinario;
import View.frmVeterinario;
import View.frmMenu;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CtrlVeterinario implements ActionListener {

    private frmVeterinario vistaVeterinario;
    private VeterinarioDAO dao;
    private Veterinario veterinarioActual;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlVeterinario(frmVeterinario vistaVeterinario, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaVeterinario = vistaVeterinario;
        this.vistaMenuAnterior = menuAnterior;
        this.dao = new VeterinarioDAO();
        this.usuarioLogueado = usuarioLogueado;
        vistaVeterinario.setLocationRelativeTo(null);
        vistaVeterinario.setResizable(false);
        vistaVeterinario.setSize(760, 777);

        // listeners
        this.vistaVeterinario.btnRegistrar.addActionListener(this);
        this.vistaVeterinario.btnModificar.addActionListener(this);
        this.vistaVeterinario.btnBuscar.addActionListener(this);
        this.vistaVeterinario.btnAnular.addActionListener(this);
        this.vistaVeterinario.btnRegresa.addActionListener(this);
        this.vistaVeterinario.btnLimpiar.addActionListener(this);

        this.vistaVeterinario.txtEstado.setText("Activo");
        this.vistaVeterinario.txtEstado.setEnabled(false);

        this.vistaVeterinario.tblVeterinario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                cargarDesdeTabla();
            }
        });

        cargarTabla();
        inicializarConsecutivoId();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaVeterinario.btnRegistrar) {
            registrar();
        }
        if (e.getSource() == vistaVeterinario.btnModificar) {
            modificar();
        }
        if (e.getSource() == vistaVeterinario.btnBuscar) {
            buscar();
        }
        if (e.getSource() == vistaVeterinario.btnAnular) {
            anular();
        }
        if (e.getSource() == vistaVeterinario.btnRegresa) {
            regresar();
        }
        if (e.getSource() == vistaVeterinario.btnLimpiar) {
            limpiar();
        }
    }

    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vistaVeterinario.tblVeterinario.getModel();
        modelo.setRowCount(0);
        List<Object[]> lista = dao.listarTodos();
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }

    private void registrar() {
        try {
            // Obtener los valores de los campos de texto
            int id = Integer.parseInt(vistaVeterinario.txtId.getText());
            String nombre = vistaVeterinario.txtNombre.getText();
            String email = vistaVeterinario.txtCorreo.getText();
            String telefono = vistaVeterinario.txtTelefono.getText();
            String veterinaria = vistaVeterinario.txtVeterinaria.getText();
            String direccion = vistaVeterinario.txtDireccion.getText();

            // 1. Validar que los campos requeridos no estén vacíos (del requerimiento anterior)
            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(vistaVeterinario, "Error: Los campos nombre, email, teléfono y dirección no pueden estar vacíos.");
                return;
            }

            // 2. Validar que el email no exista en la base de datos (nuevo requerimiento)
            if (dao.existeEmail(email)) {
                JOptionPane.showMessageDialog(vistaVeterinario, "El hay un veterinario con el mismo correo.");
                return; // Detiene la ejecución si el email es un duplicado
            }

            // Crear el objeto Veterinario
            Veterinario v = new Veterinario(id, nombre, email, telefono, veterinaria, direccion);
            v.setUsu_crea(usuarioLogueado);

            // 3. Intentar insertar el veterinario en la base de datos si las validaciones pasaron
            if (dao.insertarVeterinario(v)) {
                JOptionPane.showMessageDialog(vistaVeterinario, "El veterinario se ha registrado con exito.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(vistaVeterinario, "Error al registrar.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaVeterinario, "ID inválido.");
        }
    }

    private void modificar() {
        // 1. Validar que se ha seleccionado un veterinario para modificar
        if (veterinarioActual == null) {
            JOptionPane.showMessageDialog(vistaVeterinario, "Debe seleccionar un veterinario para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Mostrar el primer mensaje de alerta para confirmar la modificación
        int confirmacion = JOptionPane.showConfirmDialog(
                vistaVeterinario,
                "¿Está seguro de modificar el registro?",
                "Confirmar Modificación",
                JOptionPane.YES_NO_OPTION
        );

        // 3. Evaluar la respuesta del usuario
        if (confirmacion == JOptionPane.YES_OPTION) {
            // El usuario ha confirmado, se procede con la modificación

            // Actualizar el objeto veterinarioActual con los nuevos datos del formulario
            veterinarioActual.setNombre(this.vistaVeterinario.txtNombre.getText());
            veterinarioActual.setTelefono(this.vistaVeterinario.txtTelefono.getText());
            veterinarioActual.setVeterinaria(this.vistaVeterinario.txtVeterinaria.getText());
            veterinarioActual.setDireccion(this.vistaVeterinario.txtDireccion.getText());

            // Llamar al método del DAO para guardar los cambios en la base de datos
            if (this.dao.modificarVeterinario(veterinarioActual)) {
                // Si la modificación fue exitosa, mostrar un mensaje de éxito.
                JOptionPane.showMessageDialog(vistaVeterinario, "Veterinario modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                cargarTabla();
            } else {
                // Si hubo un error en la base de datos, mostrar un mensaje de error.
                JOptionPane.showMessageDialog(vistaVeterinario, "Error al modificar el veterinario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si el usuario no confirma (opción NO), no hacemos nada y la aplicación
            // queda en el mismo estado para que pueda seguir modificando los campos.
            // No es necesario un mensaje adicional, solo se cierra el cuadro de diálogo.
        }
    }

    private void buscar() {
        String email = vistaVeterinario.txtCorreo.getText();
        veterinarioActual = dao.buscarPorEmail(email);
        if (veterinarioActual != null) {
            vistaVeterinario.txtId.setText(String.valueOf(veterinarioActual.getId()));
            vistaVeterinario.txtNombre.setText(veterinarioActual.getNombre());
            vistaVeterinario.txtTelefono.setText(veterinarioActual.getTelefono());
            vistaVeterinario.txtVeterinaria.setText(veterinarioActual.getVeterinaria());
            vistaVeterinario.txtDireccion.setText(veterinarioActual.getDireccion());
            vistaVeterinario.txtEstado.setText(veterinarioActual.getEstado());
        } else {
            JOptionPane.showMessageDialog(vistaVeterinario, "El registro no existe.");
        }
    }

    private void anular() {

        if (veterinarioActual == null) {

            JOptionPane.showMessageDialog(vistaVeterinario, "Debe seleccionar un veterinario.");
            return;
        }

        if (dao.anularVeterinario(veterinarioActual.getEmail(), usuarioLogueado)) {
            JOptionPane.showMessageDialog(vistaVeterinario, "Veterinario anulado.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vistaVeterinario, "Error al anular.");
        }
    }

    private void cargarDesdeTabla() {
        int fila = vistaVeterinario.tblVeterinario.getSelectedRow();
        if (fila != -1) {

            String correoSeleccionado = vistaVeterinario.tblVeterinario.getValueAt(fila, 2).toString();

            veterinarioActual = dao.buscarPorEmail(correoSeleccionado);

            if (veterinarioActual != null) {
                vistaVeterinario.txtId.setText(String.valueOf(veterinarioActual.getId()));
                vistaVeterinario.txtNombre.setText(veterinarioActual.getNombre());
                vistaVeterinario.txtCorreo.setText(veterinarioActual.getEmail());
                vistaVeterinario.txtTelefono.setText(veterinarioActual.getTelefono());
                vistaVeterinario.txtVeterinaria.setText(veterinarioActual.getVeterinaria());
                vistaVeterinario.txtDireccion.setText(veterinarioActual.getDireccion());
                vistaVeterinario.txtEstado.setText(veterinarioActual.getEstado());
            }
        } else {
            // Si no hay fila seleccionada, limpiamos y reseteamos la variable.
            limpiar();
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = dao.obtenerSiguienteId();
        vistaVeterinario.txtId.setText(String.valueOf(siguienteId));
        vistaVeterinario.txtId.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiar() {
        vistaVeterinario.txtNombre.setText("");
        vistaVeterinario.txtCorreo.setText("");
        vistaVeterinario.txtTelefono.setText("");
        vistaVeterinario.txtVeterinaria.setText("");
        vistaVeterinario.txtDireccion.setText("");
        veterinarioActual = null;
        inicializarConsecutivoId();
    }

    // Método regresar al menú principal
    private void regresar() {
        vistaVeterinario.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }
}
