package PersistenceLayer;

import Kernel.Incidente;
import Kernel.Residente;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResidenteFileStore {

    public void guardar(Residente residente) throws IOException {
        File archivo = RutasPersistencia.archivoResidente();

        if (residente == null) {
            Files.deleteIfExists(archivo.toPath());
            return;
        }

        List<String> lines = new ArrayList<>();
        lines.add(TextCodec.join(
                "RESIDENTE",
                String.valueOf(residente.getId()),
                residente.getNombre(),
                residente.getCorreo(),
                residente.getTelefono(),
                residente.getPasswordHash()
        ));

        StringBuilder incidentes = new StringBuilder();
        for (int i = 0; i < residente.getCantidadIncidentes(); i++) {
            Incidente incidente = residente.getIncidentes()[i];
            if (incidente == null) {
                continue;
            }
            if (incidentes.length() > 0) {
                incidentes.append(',');
            }
            incidentes.append(incidente.getId());
        }
        lines.add(TextCodec.join("INCIDENTES", incidentes.toString()));

        Files.write(archivo.toPath(), lines, StandardCharsets.UTF_8);
    }

    public Residente cargar(Map<Integer, Incidente> incidentesPorId) throws IOException {
        File archivo = RutasPersistencia.archivoResidente();

        if (!archivo.exists()) {
            return null;
        }

        List<String> lines = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
        Residente residente = null;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] partes = TextCodec.split(line);
            if (partes.length == 0) {
                continue;
            }

            if ("RESIDENTE".equals(partes[0]) && partes.length >= 6) {
                int id = Integer.parseInt(partes[1]);
                residente = new Residente(id, partes[2], partes[3], partes[4], partes[5]);
            } else if ("INCIDENTES".equals(partes[0]) && residente != null && partes.length >= 2) {
                String lista = partes[1];
                if (!lista.trim().isEmpty()) {
                    String[] ids = lista.split(",");
                    for (String idTexto : ids) {
                        if (idTexto.trim().isEmpty()) {
                            continue;
                        }
                        int id = Integer.parseInt(idTexto.trim());
                        Incidente incidente = incidentesPorId.get(id);
                        if (incidente != null) {
                            residente.agregarIncidente(incidente);
                        }
                    }
                }
            }
        }

        return residente;
    }
}
