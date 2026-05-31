package PersistenceLayer;

import Kernel.Brigadista;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BrigadistaFileStore {

    public void guardar(List<Brigadista> brigadistas) throws IOException {
        File archivo = RutasPersistencia.archivoBrigadistas();
        List<String> lines = new ArrayList<>();

        for (Brigadista brigadista : brigadistas) {
            lines.add(TextCodec.join(
                    "BRIGADISTA",
                    String.valueOf(brigadista.getId()),
                    brigadista.getNombre(),
                    brigadista.getCorreo(),
                    brigadista.getTelefono(),
                    brigadista.getPasswordHash(),
                    String.valueOf(brigadista.isDisponible())
            ));
        }

        Files.write(archivo.toPath(), lines, StandardCharsets.UTF_8);
    }

    public List<Brigadista> cargar() throws IOException {
        File archivo = RutasPersistencia.archivoBrigadistas();
        List<Brigadista> brigadistas = new ArrayList<>();

        if (!archivo.exists()) {
            return brigadistas;
        }

        List<String> lines = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] partes = TextCodec.split(line);
            if (partes.length < 7) {
                continue;
            }
            if (!"BRIGADISTA".equals(partes[0])) {
                continue;
            }

            int id = Integer.parseInt(partes[1]);
            Brigadista brigadista = new Brigadista(id, partes[2], partes[3], partes[4], partes[5]);
            boolean disponible = Boolean.parseBoolean(partes[6]);
            brigadista.cambiarDisponibilidad(disponible);
            brigadistas.add(brigadista);
        }

        return brigadistas;
    }
}

