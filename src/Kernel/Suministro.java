package Kernel;

/**
 * Representa recursos de emergencia
 */
public class Suministro { //Constructor

	//Sea cual sea el suministro, debe tener código,nombre y cantidad
    private String id; 
    private String nombre;
    private int stock;
    private int stockMinimo;

    public Suministro(String id, String nombre, int stock, int stockMinimo) {
        this.setId(id);
        this.nombre = nombre;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public void agregarStock(int cantidad) {
        stock += cantidad; //Sí agrega que suba contador
    }

    public void usarStock(int cantidad) {
        if (cantidad <= stock) { //Sí sacan del stock que baje contador
            stock -= cantidad;
        } else {
            System.out.println("No hay suficiente stock");
        }

        if (stock < stockMinimo) {
            System.out.println("ALERTA: Stock bajo de " + nombre);
        }
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}