package Controller;

import View.frmMenu;
import View.frmVeterinario;
import View.frmMascota;
import View.frmEspecie;
import View.frmRaza;
import View.frmUsuario;
import View.frmPerfil;
import View.frmParametro;
import View.frmFormaPago;
import View.frmConcepto;
import View.frmExamen;
import View.frmOrdenServicio;
import View.frmLogin; // Para cerrar sesión

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CtrlMenu implements ActionListener {

    private frmMenu vistaMenu;
    private String usuarioLogueado;

    // Constructor que recibe la Vista del Menú y el usuario logueado
    public CtrlMenu(frmMenu vistaMenu, String usuarioLogueado) {
        this.vistaMenu = vistaMenu;
        this.usuarioLogueado = usuarioLogueado;

        // ⭐ Inicializar el texto al abrir el menú (Opción B)
        if (vistaMenu.lblNombreUsuario != null) { // Si usas Opción A
            vistaMenu.lblNombreUsuario.setText("Bienvenid@, " + usuarioLogueado);
        } else { // Si usas Opción B (setter)
            setNombreUsuario(usuarioLogueado);
        }

        this.vistaMenu.jmiEspecie.addActionListener(this);
        this.vistaMenu.jmiPerfil.addActionListener(this);
        this.vistaMenu.jmiRaza.addActionListener(this);
        this.vistaMenu.jmiUsuario.addActionListener(this);
        this.vistaMenu.jmiVeterinario.addActionListener(this);
        this.vistaMenu.jmiMascota.addActionListener(this);
        this.vistaMenu.jmiExamen.addActionListener(this);
        this.vistaMenu.jmiParametroExamen.addActionListener(this);
        this.vistaMenu.jmiFormaPago.addActionListener(this);
        this.vistaMenu.jmiConcepto.addActionListener(this);
        this.vistaMenu.jmiOrdenServicio.addActionListener(this);
        this.vistaMenu.jmiComprobanteEgreso.addActionListener(this);
        //this.vistaMenu.jmCerrarSesion.addActionListener(this);
        this.vistaMenu.btnCerrarSesion.addActionListener(this);

    }

    public void iniciar(String usuario) {
        vistaMenu.setTitle("Menu");
        vistaMenu.setLocationRelativeTo(null);
        vistaMenu.setResizable(false);
        vistaMenu.setSize(720, 540);
        this.usuarioLogueado = usuario;
        setNombreUsuario(usuario);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vistaMenu.jmiEspecie) {
            abrirEspecie();
        }

        if (e.getSource() == vistaMenu.jmiPerfil) {
            abrirPerfil();
        }

        if (e.getSource() == vistaMenu.jmiRaza) {
            abrirRaza();
        }

        if (e.getSource() == vistaMenu.jmiUsuario) {
            abrirUsuario();
        }

        if (e.getSource() == vistaMenu.jmiVeterinario) {
            abrirVeterinario();
        }

        if (e.getSource() == vistaMenu.jmiMascota) {
            abrirMascota();
        }

        if (e.getSource() == vistaMenu.jmiExamen) {
            abrirExamen();
        }

        if (e.getSource() == vistaMenu.jmiParametroExamen) {
            abrirParametroExamen();
        }

        if (e.getSource() == vistaMenu.jmiFormaPago) {
            abrirFormaPago();
        }

        if (e.getSource() == vistaMenu.jmiConcepto) {
            abrirConcepto();
        }

        if (e.getSource() == vistaMenu.jmiOrdenServicio) {
            abrirOrdenServicio();
        }

        if (e.getSource() == vistaMenu.jmiComprobanteEgreso) {

        }

        /*if (e.getSource() == vistaMenu.jmCerrarSesion) {
            cerrarSesion();
        }*/
        if (e.getSource() == vistaMenu.btnCerrarSesion) {
            cerrarSesion();
        }

    }

    public void setNombreUsuario(String nombre) {
        // Formato de bienvenida es "Bienvenid@, nombreUsuario"
        vistaMenu.lblNombreUsuario.setText("Bienvenid@, " + nombre);
    }

    // Métodos para abrir cada funcionalidad (contienen la lógica del MVC)
    private void abrirVeterinario() {
        frmVeterinario vista = new frmVeterinario();
        // Nota: Solo se necesita importar los Ctrl... que inician la ventana.
        CtrlVeterinario ctrl = new CtrlVeterinario(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirMascota() {
        frmMascota vista = new frmMascota();
        CtrlMascota ctrl = new CtrlMascota(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    // ... Implementar métodos abrirEspecie(), abrirRaza(), etc., siguiendo el mismo patrón...
    private void abrirExamen() {
        frmExamen vista = new frmExamen();
        CtrlExamen ctrl = new CtrlExamen(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirUsuario() {
        frmUsuario vista = new frmUsuario();
        CtrlUsuario ctrl = new CtrlUsuario(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirRaza() {
        frmRaza vista = new frmRaza();
        CtrlRaza ctrl = new CtrlRaza(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirEspecie() {
        frmEspecie vista = new frmEspecie();
        CtrlEspecie ctrl = new CtrlEspecie(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirPerfil() {
        frmPerfil vista = new frmPerfil();
        CtrlPerfil ctrl = new CtrlPerfil(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirParametroExamen() {
        frmParametro vista = new frmParametro();
        CtrlParametro ctrl = new CtrlParametro(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirFormaPago() {
        frmFormaPago vista = new frmFormaPago();
        CtrlFormaPago ctrl = new CtrlFormaPago(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirConcepto() {
        frmConcepto vista = new frmConcepto();
        CtrlConcepto ctrl = new CtrlConcepto(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.setVisible(false);
    }

    private void abrirOrdenServicio() {
        frmOrdenServicio vista = new frmOrdenServicio();
        CtrlOrdenServicio ctrl = new CtrlOrdenServicio(vista, this.vistaMenu, usuarioLogueado);
        vista.setVisible(true);
        vistaMenu.dispose();
    }
    private void cerrarSesion() {

        vistaMenu.dispose();
        frmLogin vistaLogin = new frmLogin();
        CtrlLogin ctrlLogin = new CtrlLogin(vistaLogin);
        vistaLogin.setLocationRelativeTo(null);
        vistaLogin.setVisible(true);

    }
}
