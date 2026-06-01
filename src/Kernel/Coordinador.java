package Kernel;

/**
 * Clase Coordinador que hereda de Usuario.
 */
public class Coordinador extends Usuario {

    public Coordinador(int id, String nombre, String correo, String telefono, String passwordHash) {
        super(id, nombre, correo, telefono, passwordHash, Usuario.ROL_COORDINADOR);
    }
}

