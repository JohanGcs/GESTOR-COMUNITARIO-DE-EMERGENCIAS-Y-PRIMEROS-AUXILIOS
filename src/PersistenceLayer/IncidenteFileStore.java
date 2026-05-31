package PersistenceLayer;

import Kernel.Bitacora;
import Kernel.Incidente;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class IncidenteFileStore {

    public void guardar(Incidente[] incidentes, int cantidad) throws IOException {
        File archivo = RutasPersistencia.archivoIncidentes();
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Incidente incidente = incidentes[i];
            lines.add(TextCodec.join(
                    "INCIDENTE",
                    String.valueOf(incidente.getId()),
                    incidente.getTipo(),
                    incidente.getSeveridad(),
                    incidente.getPrioridad(),
                    incidente.getUbicacion(),
                    incidente.getDescripcion(),
                    incidente.getEstado(),
                    incidente.getFechaCreacion()
            ));

            for (int j = 0; j < incidente.getCantidadBitacoras(); j++) {
                Bitacora bitacora = incidente.getBitacoras()[j];
                lines.add(TextCodec.join(
                        "BITACORA",
                        String.valueOf(bitacora.getId()),
                        bitacora.getAccion(),
                        bitacora.getDetalle(),
                        bitacora.getFecha()
                ));
            }

            lines.add("END");
        }

        Files.write(archivo.toPath(), lines, StandardCharsets.UTF_8);
    }

    public List<Incidente> cargar() throws IOException {
        File archivo = RutasPersistencia.archivoIncidentes();
        List<Incidente> incidentes = new ArrayList<>();

        if (!archivo.exists()) {
            return incidentes;
        }

        List<String> lines = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
        Incidente actual = null;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] partes = TextCodec.split(line);
            if (partes.length == 0) {
                continue;
            }

            String tipoRegistro = partes[0];
            if ("INCIDENTE".equals(tipoRegistro)) {
                if (partes.length < 9) {
                    continue;
                }
                int id = Integer.parseInt(partes[1]);
                actual = new Incidente(
                        id,
                        partes[2],
                        partes[3],
                        partes[4],
                        partes[5],
                        partes[6],
                        partes[7],
                        partes[8]
                );
            } else if ("BITACORA".equals(tipoRegistro) && actual != null) {
                if (partes.length < 5) {
                    continue;
                }
                int id = Integer.parseInt(partes[1]);
                Bitacora bitacora = new Bitacora(id, partes[2], partes[3], partes[4]);
                actual.agregarBitacora(bitacora);
            } else if ("END".equals(tipoRegistro) && actual != null) {
                incidentes.add(actual);
                actual = null;
            }
        }

        if (actual != null) {
            incidentes.add(actual);
        }

        return incidentes;
    }
}
