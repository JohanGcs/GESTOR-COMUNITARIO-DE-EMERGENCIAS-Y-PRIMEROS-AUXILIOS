package Kernel;

import java.util.Iterator;
import java.util.List;

public class Reporte {

    /**
     * Cuenta incidentes por tipo
     */
    public void generarPorTipo(List<Incidente> lista, String tipo) {

        int contador = 0;

        for (Iterator<Incidente> iterator = lista.iterator(); iterator.hasNext();) {
			// Que recorra incidentes y asigne
            contador++;
		}

        System.out.println("Total de incidentes tipo " + tipo + ": " + contador);
    }
}