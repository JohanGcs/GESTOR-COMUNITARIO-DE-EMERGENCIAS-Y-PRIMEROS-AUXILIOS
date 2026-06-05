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

    /**
     * Sobreescribe getDescripcionRol de Usuario.
     * El brigadista atiende incidentes y actualiza el estado de las intervenciones.
     */
    @Override
    public String getDescripcionRol() {
        return "Brigadista: atiende incidentes asignados y registra el avance de la intervencion.";
    }

    /**
     * Sobreescribe editarPerfil de Usuario.
     * Al editar el perfil del brigadista tambien se le reactiva la disponibilidad.
     */
    @Override
    public void editarPerfil(String nombre, String correo, String telefono) {
        super.editarPerfil(nombre, correo, telefono);
        // Al actualizar el perfil el brigadista queda disponible nuevamente.
        this.disponible = true;
    }

    /**
     * Sobreescribe toString de Usuario para incluir el estado de disponibilidad.
     */
    @Override
    public String toString() {
        return "[BRIGADISTA] " + getNombre() + " (" + getCorreo() + ") - Disponible: " + (disponible ? "SI" : "NO");
    }
}
