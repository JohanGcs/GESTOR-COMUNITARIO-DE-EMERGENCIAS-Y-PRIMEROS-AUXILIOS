package Kernel;

import java.time.LocalDateTime; //Ajuste en tiempo real

public class MovimientoInventario {

    private String tipo; // "entrada" o "salida"
    private int cantidad; //Cuantos saca
    private LocalDateTime fecha;
    private Suministro suministro;

    public MovimientoInventario(String tipo, int cantidad, Suministro suministro) { //Los cambios de suministro
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.suministro = suministro;
        this.setFecha(LocalDateTime.now());
    }

    public void aplicar() {

        if (tipo.equalsIgnoreCase("entrada")) {
            suministro.agregarStock(cantidad);
        } else if (tipo.equalsIgnoreCase("salida")) {
            suministro.usarStock(cantidad);
        } else {
            System.out.println("Tipo inválido");
        }
    }

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
}