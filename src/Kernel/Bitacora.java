package modelo;

import java.time.LocalDateTime; //Incidentes pasan en tiempo real

/**
 * CLASE: Bitacora
 * 
 * Representa el HISTORIAL del incidente.
 * 
 *  Cada acción importante se guarda aquí:
 * - asignaciones
 * - inicio de atención
 * - cierre
 * 
 * Lo que nos da:
 *  Auditoría
 *  Seguimiento
 *  Transparencia del sistema
 */
public class Bitacora {


    // ATRIBUTOS
   

    private String accion;     // qué pasó (Asignación, Inicio, Cierre)
    private String detalle;    // descripción más específica
    private LocalDateTime fecha; // cuándo ocurrió


    // CONSTRUCTOR
 

    public Bitacora(String accion, String detalle) {
        this.accion = accion;
        this.detalle = detalle;
        this.fecha = LocalDateTime.now();
    }


  
    // COMPORTAMIENTO
 

    /**
     * Muestra el registro
   */
    
    public void mostrar() {
        System.out.println(fecha + " | " + accion + " | " + detalle);
    }


    // GETTERS 
    public String getAccion() {
        return accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}