package PersistenceLayer;

import java.io.File;

public final class RutasPersistencia {

    private static final String NOMBRE_CARPETA = "GestorEmergenciasData";

    private RutasPersistencia() {
    }

    public static File baseDir() {
        File baseDir = new File(System.getProperty("user.home"), NOMBRE_CARPETA);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        return baseDir;
    }

    public static File archivoIncidentes() {
        return new File(baseDir(), "incidentes.txt");
    }

    public static File archivoResidente() {
        return new File(baseDir(), "residente.txt");
    }

    public static File archivoInventario() {
        return new File(baseDir(), "inventario.txt");
    }

    public static File archivoBrigadistas() {
        return new File(baseDir(), "brigadistas.txt");
    }

    public static File archivoAsignaciones() {
        return new File(baseDir(), "asignaciones.txt");
    }

    public static File archivoUsuarios() {
        return new File(baseDir(), "usuarios.txt");
    }
}
