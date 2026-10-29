/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import Controller.CtrlLogin;
import View.frmLogin;

public class Compilador {

    // Ejemplo de la clase main o donde se inicia el login
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // 1. Crear la Vista
                frmLogin vista = new frmLogin();

                // 2. Crear el Controlador y conectarlo a la Vista
                CtrlLogin controlador = new CtrlLogin(vista);

                // 3. Mostrar la Vista
                vista.setVisible(true);
            }
        });
    }
}
