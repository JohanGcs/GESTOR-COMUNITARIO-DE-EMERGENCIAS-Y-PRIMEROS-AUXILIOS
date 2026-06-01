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
    private int idIncidente;

  
    // CONSTRUCTOR
 

    public Asignacion(Brigadista brigadista) {
        this(brigadista, -1);
    }

    public Asignacion(Brigadista brigadista, int idIncidente) {
        this.brigadista = brigadista;
        this.estado = "pendiente"; // estado inicial
        this.fecha = LocalDateTime.now();
        this.idIncidente = idIncidente;
    }

    public Asignacion(Brigadista brigadista, int idIncidente, String estado, LocalDateTime fecha) {
        this.brigadista = brigadista;
        this.estado = estado;
        this.fecha = fecha;
        this.idIncidente = idIncidente;
    }

    public void aceptar() {
        estado = "aceptada";
        if (brigadista != null) {
            brigadista.cambiarDisponibilidad(false);
        }
    }

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

    public int getIdIncidente() {
        return idIncidente;
    }
}