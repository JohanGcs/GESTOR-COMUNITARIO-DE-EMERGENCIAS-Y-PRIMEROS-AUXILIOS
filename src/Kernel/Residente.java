package Kernel;

/**
 * Clase Residente que hereda de Usuario.
 * Puede reportar incidentes asociados a su cuenta.
 */
public class Residente extends Usuario {

    // Atributos
    private Incidente[] incidentes;
    private int cantidadIncidentes;

    // Constructor
    public Residente(int id, String nombre, String correo, String telefono, String passwordHash) {
        // Llama al constructor de la clase base Usuario.
        super(id, nombre, correo, telefono, passwordHash, Usuario.ROL_RESIDENTE);
        // Inicializa el arreglo de incidentes reportados.
        this.incidentes = new Incidente[50];
        this.cantidadIncidentes = 0;
    }

    // Metodos
    public void agregarIncidente(Incidente incidente) {
        // Guarda un incidente si hay espacio disponible.
        if (cantidadIncidentes < incidentes.length) {
            incidentes[cantidadIncidentes] = incidente;
            cantidadIncidentes = cantidadIncidentes + 1;
        }
    }

    public Incidente[] getIncidentes() {
        return incidentes;
    }

    public int getCantidadIncidentes() {
        return cantidadIncidentes;
    }
}
