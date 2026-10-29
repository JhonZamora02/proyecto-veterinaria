package Controller;

import View.frmLogin;
import View.frmMenu;
import DAO.LoginDAO;
import Controller.CtrlMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

public class CtrlLogin implements ActionListener {

    private frmLogin vistaLogin;
    private LoginDAO usuarioDAO;

    // Constructor que recibe la Vista (frmLogin)
    public CtrlLogin(frmLogin vistaLogin) {
        this.vistaLogin = vistaLogin;
        this.usuarioDAO = new LoginDAO();

        // Asignar este controlador como el escuchador del botón de la Vista
        asignarListeners();
    }

    // Método para asignar el ActionListener al botón
    private void asignarListeners() {
        vistaLogin.getBtnIngresar().addActionListener(this);
        vistaLogin.getJcbContrasena().addActionListener(this);
    }

    // Implementación del método actionPerformed para manejar el evento del botón
    @Override
    public void actionPerformed(ActionEvent evt) {

        if (evt.getSource() == vistaLogin.getJcbContrasena()) {
            if (vistaLogin.getJcbContrasena().isSelected()) {
                // Mostrar la contraseña: Establece el carácter de eco a 0 (cero)
                vistaLogin.getTxtContrasena().setEchoChar((char) 0);
            } else {
                // Ocultar la contraseña: Establece el carácter de eco al predeterminado (punto/asterisco)
                vistaLogin.getTxtContrasena().setEchoChar('*');
            }
            return; // Salir para no intentar iniciar sesión si solo se hizo clic en el CheckBox
        }

        if (evt.getSource() == vistaLogin.getBtnIngresar()) {
            String usuario = vistaLogin.getTxtUsuario().getText();
            // Obtiene la contraseña desde el campo de contraseña
            String contrasena = new String(vistaLogin.getTxtContrasena().getPassword());

            // Aquí llamamos al método autenticar de esta misma clase
            boolean autenticacionExitosa = autenticar(usuario, contrasena);

            //Evaluar el resultado 
            if (autenticacionExitosa) {
                JOptionPane.showMessageDialog(vistaLogin, "¡Inicio de sesión exitoso!", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);

                frmMenu menuPrincipal = new frmMenu();
                CtrlMenu objCtrl = new CtrlMenu(menuPrincipal, usuario);
                menuPrincipal.setVisible(true);
                objCtrl.iniciar(usuario);
                vistaLogin.dispose();

            } else {
                JOptionPane.showMessageDialog(vistaLogin,
                        "Credenciales incorrectas o usuario INACTIVO.",
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE);

                vistaLogin.getTxtUsuario().setText("");
                vistaLogin.getTxtContrasena().setText("");
                vistaLogin.getTxtUsuario().requestFocus();
            }
        }

    }

    // Método de autenticación (se mantiene y se usa dentro del actionPerformed)
    public boolean autenticar(String usuario, String contrasena) {
        // ...
        boolean resultado = usuarioDAO.validarCredenciales(usuario, contrasena);
        System.out.println("Intento de Login: " + usuario + ", Resultado: " + resultado); // <--- AÑADE ESTO
        return resultado;
    }
}
