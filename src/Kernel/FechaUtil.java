package Kernel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para formatear fecha y hora.
 */
public class FechaUtil {

    // Formato comun de fecha y hora.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String ahora() {
        // Retorna la fecha/hora actual en el formato definido.
        return LocalDateTime.now().format(FORMATO);
    }
}
