package Kernel;


import java.util.List;

public class Coordinador extends Usuario { //Coordinador parte de usuario

    public Coordinador(String id, String nombre) {
        super(id, nombre);
    }

    @Override
    public void accion() { //Multiples acciones
        System.out.println(getNombre() + " coordina el sistema");
    }

    /**
     * Asignar brigadista
     */
    public void asignar(Incidente i, Brigadista b) {
        i.asignarBrigadista(b);
    }

    /**
     * Buscar brigadista disponible
     */
    public Brigadista buscarDisponible(List<Brigadista> lista) {

        for (Brigadista b : lista) {
            if (b.isDisponible()) {
                return b;
            }
        }

        return null;
    }
}