package Kernel;

/**
 * Clase abstracta base para todos los usuarios del sistema.
 * Define atributos comunes y un metodo de edicion de perfil.
 */
public abstract class Usuario {

    // Constantes de rol (sin enums)
    public static final String ROL_RESIDENTE = "RESIDENTE";
    public static final String ROL_BRIGADISTA = "BRIGADISTA";
    public static final String ROL_COORDINADOR = "COORDINADOR";

    // Atributos
    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String passwordHash;
    private String rol;

    // Constructor
    public Usuario(int id, String nombre, String correo, String telefono, String passwordHash, String rol) {
        // Identificacion basica
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        // Seguridad y rol
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRol() {
        return rol;
    }

    // Setters de perfil
    public void editarPerfil(String nombre, String correo, String telefono) {
        // Actualiza informacion de contacto.
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    /**
     * Devuelve una descripcion del rol y responsabilidades del usuario.
     * Cada subclase sobreescribe este metodo para dar su propia descripcion.
     */
    public abstract String getDescripcionRol();

    @Override
    public String toString() {
        // Representacion basica del usuario; las subclases pueden enriquecer esto.
        return "[" + rol + "] " + nombre + " (" + correo + ")";
    }
}
