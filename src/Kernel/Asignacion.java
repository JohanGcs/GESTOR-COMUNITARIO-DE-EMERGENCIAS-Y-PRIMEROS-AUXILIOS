package Kernel;

import java.time.LocalDateTime; //A la fecha del incidente

/**
 * CLASE: Asignacion
 * Representa la RELACIÓN entre un Brigadista y un Incidente
 * NO guardamos brigadistas directamente en Incidente sino en Asignacion.
 *  Porque la relación tiene ESTADO:  pendiente aceptada rechazada
 */
public class Asignacion {

   
    // ATRIBUTOS
   

    private Brigadista brigadista;  // quién atiende
    private String estado;          // estado de la asignación
    private LocalDateTime fecha;    // cuándo se hizo


  
    // CONSTRUCTOR
 

    public Asignacion(Brigadista brigadista) {
        this.brigadista = brigadista;
        this.estado = "pendiente"; // estado inicial
        this.fecha = LocalDateTime.now();
    }



    // COMPORTAMIENTO


    /**
     * El brigadista acepta la asignación
     * 
     * REGLA: cambia estado se marca como NO disponible
     */

    /**
     * El brigadista rechaza la asignación
     * 
     * REGLA:
     * - solo cambia estado
     * - sigue disponible
     */
    public void rechazar() {
        estado = "rechazada";
    }


 
    // GETTERS 
    

    public Brigadista getBrigadista() {
        return brigadista;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}