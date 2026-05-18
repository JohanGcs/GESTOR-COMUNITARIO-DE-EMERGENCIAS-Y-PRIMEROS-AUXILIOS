package Kernel;

/**
 * Clase Guia de primeros auxilios.
 */
public class Guia {

    // Atributos
    private int id;
    private String tipoEmergencia;
    private String titulo;
    private String contenido;

    // Constructor
    public Guia(int id, String tipoEmergencia, String titulo, String contenido) {
        // Datos de la guia.
        this.id = id;
        this.tipoEmergencia = tipoEmergencia;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTipoEmergencia() {
        return tipoEmergencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public String resumen() {
        // Formato completo para mostrar la guia.
        return "Guia " + id + " | " + titulo + " | Tipo: " + tipoEmergencia + "\n" + contenido;
    }
}
