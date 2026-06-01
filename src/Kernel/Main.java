package Kernel;

import GUI.LoginFrame;
import java.awt.EventQueue;

/**
 * Clase principal que inicia la interfaz grafica del sistema.
 */
public class Main {

    // Metodo principal
    public static void main(String[] args) {
        // Ejecuta la ventana en el hilo de eventos de Swing.
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame ventana = new LoginFrame();
                ventana.setVisible(true);
            }
        });
    }
}