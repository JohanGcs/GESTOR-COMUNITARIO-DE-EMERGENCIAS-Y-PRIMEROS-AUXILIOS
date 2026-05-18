package Kernel;

/**
 * Clase Incidente que representa una emergencia reportada.
 */
public class Incidente {

    // Estados (sin enums)
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ASIGNADO = "ASIGNADO";
    public static final String ESTADO_EN_CAMINO = "EN_CAMINO";
    public static final String ESTADO_EN_INTERVENCION = "EN_INTERVENCION";
    public static final String ESTADO_FINALIZADO = "FINALIZADO";
    public static final String ESTADO_CANCELADO = "CANCELADO";

    // Atributos
    private int id;
    private String tipo;
    private String severidad;
    private String prioridad;
    private String ubicacion;
    private String descripcion;
    private String estado;
    private String fechaCreacion;

    private Bitacora[] bitacoras;
    private int cantidadBitacoras;

    // Constructor
    public Incidente(int id, String tipo, String severidad, String prioridad, String ubicacion, String descripcion, String estado, String fechaCreacion) {
        // Datos principales del incidente.
        this.id = id;
        this.tipo = tipo;
        this.severidad = severidad;
        this.prioridad = prioridad;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        // Inicializa la bitacora del incidente.
        this.bitacoras = new Bitacora[200];
        this.cantidadBitacoras = 0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSeveridad() {
        return severidad;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public int getCantidadBitacoras() {
        return cantidadBitacoras;
    }

    public Bitacora[] getBitacoras() {
        return bitacoras;
    }

    // Metodos
    public void actualizarEstado(String nuevoEstado) {
        // Cambia el estado del incidente.
        this.estado = nuevoEstado;
    }

    public void agregarBitacora(Bitacora bitacora) {
        // Agrega un registro si hay espacio en el arreglo.
        if (cantidadBitacoras < bitacoras.length) {
            bitacoras[cantidadBitacoras] = bitacora;
            cantidadBitacoras = cantidadBitacoras + 1;
        }
    }

    public String resumen() {
        // Devuelve una linea resumen para listados.
        return "#" + id + " | " + tipo + " | " + prioridad + " | " + estado + " | " + ubicacion;
    }
}