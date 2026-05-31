package PersistenceLayer;

import Kernel.Asignacion;
import Kernel.Brigadista;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsignacionFileStore {

    public void guardar(List<Asignacion> asignaciones) throws IOException {
        File archivo = RutasPersistencia.archivoAsignaciones();
        List<String> lines = new ArrayList<>();

        for (Asignacion asignacion : asignaciones) {
            lines.add(TextCodec.join(
                    "ASIGNACION",
                    String.valueOf(asignacion.getIdIncidente()),
                    String.valueOf(asignacion.getBrigadista().getId()),
                    asignacion.getEstado(),
                    asignacion.getFecha().toString()
            ));
        }

        Files.write(archivo.toPath(), lines, StandardCharsets.UTF_8);
    }

    public List<Asignacion> cargar(Map<Integer, Brigadista> brigadistas) throws IOException {
        File archivo = RutasPersistencia.archivoAsignaciones();
        List<Asignacion> asignaciones = new ArrayList<>();

        if (!archivo.exists()) {
            return asignaciones;
        }

        List<String> lines = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] partes = TextCodec.split(line);
            if (partes.length < 5) {
                continue;
            }
            if (!"ASIGNACION".equals(partes[0])) {
                continue;
            }

            int idIncidente = Integer.parseInt(partes[1]);
            int idBrigadista = Integer.parseInt(partes[2]);
            Brigadista brigadista = brigadistas.get(idBrigadista);
            if (brigadista == null) {
                continue;
            }
            String estado = partes[3];
            LocalDateTime fecha = LocalDateTime.parse(partes[4]);
            asignaciones.add(new Asignacion(brigadista, idIncidente, estado, fecha));
        }

        return asignaciones;
    }
}

