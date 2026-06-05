package Kernel;

/**
 * Clase Coordinador que hereda de Usuario.
 * Supervisa incidentes, asigna brigadistas y gestiona el inventario.
 */
public class Coordinador extends Usuario {

    public Coordinador(int id, String nombre, String correo, String telefono, String passwordHash) {
        super(id, nombre, correo, telefono, passwordHash, Usuario.ROL_COORDINADOR);
    }

    /**
     * Sobreescribe getDescripcionRol de Usuario.
     * El coordinador supervisa todas las operaciones del sistema.
     */
    @Override
    public String getDescripcionRol() {
        return "Coordinador: supervisa incidentes, asigna brigadistas y gestiona el inventario de suministros.";
    }

    /**
     * Sobreescribe editarPerfil de Usuario.
     * El coordinador no puede cambiar su correo institucional desde el perfil.
     */
    @Override
    public void editarPerfil(String nombre, String correo, String telefono) {
        // El coordinador solo actualiza nombre y telefono; el correo queda intacto.
        super.editarPerfil(nombre, getCorreo(), telefono);
    }

    /**
     * Sobreescribe toString de Usuario para identificar al coordinador claramente.
     */
    @Override
    public String toString() {
        return "[COORDINADOR] " + getNombre() + " (" + getCorreo() + ")";
    }
}

