/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.PerfilDAO;
import DAO.UsuarioDAO;
import Model.Perfil;
import Model.Usuario;
import View.frmMenu;
import View.frmUsuario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CtrlUsuario implements ActionListener {

    private frmUsuario vistaUsuario;
    private UsuarioDAO daoUsuario;
    private PerfilDAO daoPerfil;
    private String usuarioLogueado;
    private frmMenu vistaMenuAnterior;

    public CtrlUsuario(frmUsuario vistaUsuario, frmMenu menuAnterior, String usuarioLogueado) {
        this.vistaUsuario = vistaUsuario;
        this.vistaMenuAnterior = menuAnterior;
        this.usuarioLogueado = usuarioLogueado;
        this.daoUsuario = new UsuarioDAO();
        this.daoPerfil = new PerfilDAO();
        vistaUsuario.setLocationRelativeTo(null);
        vistaUsuario.setResizable(false);
        vistaUsuario.setSize(1093, 914);

        // Enlazar botones de la vista con eventos
        this.vistaUsuario.btnRegistrarse.addActionListener(this);
        this.vistaUsuario.btnActualizar.addActionListener(this);
        this.vistaUsuario.btnEstado.addActionListener(this);
        this.vistaUsuario.btnLimpiar.addActionListener(this);
        this.vistaUsuario.btnRegresa.addActionListener(this);

        // Cargar datos iniciales
        cargarComboPerfil();
        cargarTabla();
        inicializarConsecutivoId();

        // Evento al hacer clic en una fila de la tabla
        this.vistaUsuario.jtbList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = vistaUsuario.jtbList.getSelectedRow();
                if (fila != -1) {
                    vistaUsuario.txtId.setText(vistaUsuario.jtbList.getValueAt(fila, 0).toString());
                    vistaUsuario.txtIdentificacion.setText(vistaUsuario.jtbList.getValueAt(fila, 1).toString());
                    vistaUsuario.txtUsuario.setText(vistaUsuario.jtbList.getValueAt(fila, 2).toString());
                    vistaUsuario.txtContraseña.setText(vistaUsuario.jtbList.getValueAt(fila, 3).toString());
                    vistaUsuario.txtNombre.setText(vistaUsuario.jtbList.getValueAt(fila, 4).toString());
                    vistaUsuario.txtApellido.setText(vistaUsuario.jtbList.getValueAt(fila, 5).toString());
                    vistaUsuario.txtCorreo.setText(vistaUsuario.jtbList.getValueAt(fila, 6).toString());
                    vistaUsuario.txtDireccion.setText(vistaUsuario.jtbList.getValueAt(fila, 7).toString());
                    vistaUsuario.txtTelefono.setText(vistaUsuario.jtbList.getValueAt(fila, 8).toString());
                    vistaUsuario.txtEstado.setText(vistaUsuario.jtbList.getValueAt(fila, 9).toString());

                    // Seleccionar el perfil en el combo usando la descripción
                    String perfilNombre = vistaUsuario.jtbList.getValueAt(fila, 10).toString();
                    for (int i = 0; i < vistaUsuario.cmbPerfil.getItemCount(); i++) {
                        if (vistaUsuario.cmbPerfil.getItemAt(i).toString().equals(perfilNombre)) {
                            vistaUsuario.cmbPerfil.setSelectedIndex(i);
                            break;
                        }
                    }

                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaUsuario.btnRegistrarse) {
            agregarUsuario();
        }
        if (e.getSource() == vistaUsuario.btnActualizar) {
            actualizarUsuario();
        }
        if (e.getSource() == vistaUsuario.btnEstado) {
            cambiarEstado();
        }
        if (e.getSource() == vistaUsuario.btnLimpiar) {
            limpiarCampos();
        }
        if (e.getSource() == vistaUsuario.btnRegresa) {
            regresar();
        }
    }

    // ===================== MÉTODOS =====================
    private void cargarComboPerfil() {
        vistaUsuario.cmbPerfil.removeAllItems();
        for (Perfil p : daoPerfil.listarActivas()) {
            vistaUsuario.cmbPerfil.addItem(p); // gracias al toString(), se verá la descripción
        }
    }

    private void cargarTabla() {
        String[] cols = {"ID", "IDENTIFICACION", "USUARIO", "CONTRASEÑA", "NOMBRE", "APELLIDO", "CORREO", "DIRECCION", "TELEFONO", "ESTADO", "PERFIL"};

        DefaultTableModel modelo = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bloquea edición directa en la tabla
            }
        };

        List<Usuario> lista = daoUsuario.listar();
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getUsu_id(),
                u.getUsu_identificacion(),
                u.getUsu_login(),
                "********",
                u.getUsu_nombre(),
                u.getUsu_apellido(),
                u.getUsu_email(),
                u.getUsu_dir(),
                u.getUsu_tel(),
                u.getUsu_estado(),
                u.getPer_descripcion(), // Usamos la descripción del perfil
            //u.getImg_firma(),
            });
        }
        vistaUsuario.jtbList.setModel(modelo);
    }

    private void agregarUsuario() {
        try {
            Usuario u = new Usuario();

            u.setUsu_id(vistaUsuario.txtId.getText());
            u.setUsu_identificacion(vistaUsuario.txtIdentificacion.getText());
            u.setUsu_login(vistaUsuario.txtUsuario.getText());
            u.setUsu_pass(vistaUsuario.txtContraseña.getText());
            u.setUsu_nombre(vistaUsuario.txtNombre.getText());
            u.setUsu_apellido(vistaUsuario.txtApellido.getText());
            u.setUsu_email(vistaUsuario.txtCorreo.getText());
            u.setUsu_dir(vistaUsuario.txtDireccion.getText());
            u.setUsu_tel(vistaUsuario.txtTelefono.getText());
            u.setUsu_estado(vistaUsuario.txtEstado.getText());
            Perfil perfil = (Perfil) vistaUsuario.cmbPerfil.getSelectedItem();
            
            if (perfil.getPer_estado().equalsIgnoreCase("ANULADO")) {
                JOptionPane.showMessageDialog(vistaUsuario,
                        "No se puede crear usuario con un perfil ANULADO");
                return;
            }
            
            u.setPer_id(perfil.getPer_id());

            if (daoUsuario.agregar(u)) {
                JOptionPane.showMessageDialog(vistaUsuario, "Usuario registrada correctamente.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vistaUsuario, "Error al registrar el usuario.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vistaUsuario, "Error: " + ex.getMessage());
        }
    }

    private void actualizarUsuario() {
        try {
            Usuario u = new Usuario();
            u.setUsu_id(vistaUsuario.txtId.getText());
            u.setUsu_identificacion(vistaUsuario.txtIdentificacion.getText());
            u.setUsu_login(vistaUsuario.txtUsuario.getText());
            u.setUsu_pass(vistaUsuario.txtContraseña.getText());
            u.setUsu_nombre(vistaUsuario.txtNombre.getText());
            u.setUsu_apellido(vistaUsuario.txtApellido.getText());
            u.setUsu_email(vistaUsuario.txtCorreo.getText());
            u.setUsu_dir(vistaUsuario.txtDireccion.getText());
            u.setUsu_tel(vistaUsuario.txtTelefono.getText());
            u.setUsu_estado(vistaUsuario.txtEstado.getText());
            Perfil perfil = (Perfil) vistaUsuario.cmbPerfil.getSelectedItem();
            
            if (perfil.getPer_estado().equalsIgnoreCase("ANULADO")) {
                JOptionPane.showMessageDialog(vistaUsuario,
                        "No se puede asignar un perfil ANULADO");
                return;
            }
            
            u.setPer_id(perfil.getPer_id());;

            if (daoUsuario.actualizar(u)) {
                JOptionPane.showMessageDialog(vistaUsuario, "Usuario actualizada.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vistaUsuario, "Error al actualizar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vistaUsuario, "Error: " + ex.getMessage());
        }
    }

    private void cambiarEstado() {
        int fila = vistaUsuario.jtbList.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaUsuario, "Seleccione una fila primero.");
            return;
        }
        // Obtener la identificación del usuario (índice 1 en la tabla)
        String id = (String) vistaUsuario.jtbList.getValueAt(fila, 0);
        String estadoActual = (String) vistaUsuario.jtbList.getValueAt(fila, 9);
        String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Inactivo" : "Activo";

        if (daoUsuario.cambiarEstado(id, nuevoEstado)) {
            JOptionPane.showMessageDialog(vistaUsuario, "Estado cambiado a " + nuevoEstado);
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(vistaUsuario, "Error al cambiar estado.");
        }
    }

    private void inicializarConsecutivoId() {
        int siguienteId = daoUsuario.obtenerSiguienteId();
        vistaUsuario.txtId.setText(String.valueOf(siguienteId));
        vistaUsuario.txtId.setEnabled(false); // Recomendado para evitar que el usuario lo modifique
    }

    private void limpiarCampos() {
        vistaUsuario.txtIdentificacion.setText("");
        vistaUsuario.txtUsuario.setText("");
        vistaUsuario.txtContraseña.setText("");
        vistaUsuario.txtNombre.setText("");
        vistaUsuario.txtApellido.setText("");
        vistaUsuario.txtCorreo.setText("");
        vistaUsuario.txtDireccion.setText("");
        vistaUsuario.txtTelefono.setText("");
        vistaUsuario.txtEstado.setText("Activo");// valor por defecto
        vistaUsuario.cmbPerfil.setSelectedIndex(-1);
        inicializarConsecutivoId();
    }

    // ===================== REGRESAR AL MENÚ =====================
    private void regresar() {
        vistaUsuario.dispose();
        if (vistaMenuAnterior.lblNombreUsuario != null) {
            vistaMenuAnterior.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        }
        vistaMenuAnterior.setVisible(true);
    }

}
