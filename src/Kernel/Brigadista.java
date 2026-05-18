package Kernel;

/**
 * Clase Brigadista que hereda de Usuario.
 * Gestiona su disponibilidad y respuestas a asignaciones.
 */
public class Brigadista extends Usuario {

    // Atributos
    private boolean disponible;

    // Constructor
    public Brigadista(int id, String nombre, String correo, String telefono, String passwordHash) {
        // Llama al constructor de la clase base Usuario.
        super(id, nombre, correo, telefono, passwordHash, Usuario.ROL_BRIGADISTA);
        // Por defecto el brigadista esta disponible.
        this.disponible = true;
    }

    // Metodos
    public boolean isDisponible() {
        return disponible;
    }

    public void cambiarDisponibilidad(boolean disponible) {
        // Actualiza el estado de disponibilidad.
        this.disponible = disponible;
    }

    public String aceptarAsignacion(int idIncidente) {
        // Simula la aceptacion de una asignacion.
        return "Asignacion aceptada para incidente " + idIncidente + ".";
    }

    public String rechazarAsignacion(int idIncidente) {
        // Simula el rechazo de una asignacion.
        return "Asignacion rechazada para incidente " + idIncidente + ".";
    }
}
