package Kernel;

/**
 * Clase ValidadorApp que centraliza reglas de entrada.
 */
public class Validador {

    public String validarTextoNoVacio(String valor, String campo) throws ValidacionException {
        // Verifica que el texto tenga contenido.
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    public int convertirEntero(String valor, String campo) throws ValidacionException {
        // Convierte texto a entero con manejo de error.
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException ex) {
            throw new ValidacionException("El campo " + campo + " debe ser un numero entero.");
        }
    }

    public void validarEnteroPositivo(int valor, String campo) throws ValidacionException {
        // Valida valores mayores que cero.
        if (valor <= 0) {
            throw new ValidacionException("El campo " + campo + " debe ser mayor a 0.");
        }
    }

    public void validarEmailBasico(String correo) throws ValidacionException {
        // Valida formato basico de correo.
        if (!correo.contains("@") || !correo.contains(".")) {
            throw new ValidacionException("Correo no valido.");
        }
    }

    public void validarTelefonoBasico(String telefono) throws ValidacionException {
        // Valida longitud minima del telefono.
        if (telefono.length() < 7) {
            throw new ValidacionException("Telefono no valido.");
        }
    }
}
