package Kernel;

public class Bitacora {

    // Atributos
    private int id;
    private String accion;
    private String detalle;
    private String fecha;

    // Constructor
    public Bitacora(int id, String accion, String detalle, String fecha) {
        // Datos del registro.
        this.id = id;
        this.accion = accion;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getAccion() {
        return accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getFecha() {
        return fecha;
    }
