package PersistenceLayer;

import Kernel.Brigadista;
import Kernel.Coordinador;
import Kernel.Residente;
import Kernel.Usuario;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class UsuarioFileStore {

    public void guardar(List<Usuario> usuarios) throws IOException {
        File archivo = RutasPersistencia.archivoUsuarios();
        List<String> lines = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            boolean disponible = true;
            if (usuario instanceof Brigadista) {
                disponible = ((Brigadista) usuario).isDisponible();
            }
            lines.add(TextCodec.join(
                    "USUARIO",
                    String.valueOf(usuario.getId()),
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    usuario.getTelefono(),
                    usuario.getPasswordHash(),
                    usuario.getRol(),
                    String.valueOf(disponible)
            ));
        }

        Files.write(archivo.toPath(), lines, StandardCharsets.UTF_8);
    }

    public List<Usuario> cargar() throws IOException {
        File archivo = RutasPersistencia.archivoUsuarios();
        List<Usuario> usuarios = new ArrayList<>();

        if (!archivo.exists()) {
            return usuarios;
        }

        List<String> lines = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] partes = TextCodec.split(line);
            if (partes.length < 8) {
                continue;
            }
            if (!"USUARIO".equals(partes[0])) {
                continue;
            }

            int id = Integer.parseInt(partes[1]);
            String nombre = partes[2];
            String correo = partes[3];
            String telefono = partes[4];
            String passwordHash = partes[5];
            String rol = partes[6];
            boolean disponible = Boolean.parseBoolean(partes[7]);

            Usuario usuario = crearUsuario(id, nombre, correo, telefono, passwordHash, rol, disponible);
            if (usuario != null) {
                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    private Usuario crearUsuario(int id, String nombre, String correo, String telefono,
                                 String passwordHash, String rol, boolean disponible) {
        if (Usuario.ROL_RESIDENTE.equalsIgnoreCase(rol)) {
            return new Residente(id, nombre, correo, telefono, passwordHash);
        }
        if (Usuario.ROL_BRIGADISTA.equalsIgnoreCase(rol)) {
            Brigadista brigadista = new Brigadista(id, nombre, correo, telefono, passwordHash);
            brigadista.cambiarDisponibilidad(disponible);
            return brigadista;
        }
        if (Usuario.ROL_COORDINADOR.equalsIgnoreCase(rol)) {
            return new Coordinador(id, nombre, correo, telefono, passwordHash);
        }
        return null;
    }
}

