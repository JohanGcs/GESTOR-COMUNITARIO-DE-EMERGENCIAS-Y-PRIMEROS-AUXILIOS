package Kernel;


import POOLabFinal.VentanaPrincipal;

import java.awt.*;

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
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            }
        });
    }
}