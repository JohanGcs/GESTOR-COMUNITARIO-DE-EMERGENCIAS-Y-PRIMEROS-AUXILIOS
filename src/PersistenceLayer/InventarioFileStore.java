package PersistenceLayer;

import Kernel.Suministro;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class InventarioFileStore {

    public void guardar(List<Suministro> suministros) throws IOException {
        File archivo = RutasPersistencia.archivoInventario();
        List<String> lines = new ArrayList<>();

        for (Suministro suministro : suministros) {
            lines.add(TextCodec.join(
                    "SUMINISTRO",
                    suministro.getId(),
                    suministro.getNombre(),
                    String.valueOf(suministro.getStock()),
                    String.valueOf(suministro.getStockMinimo())
            ));
        }

        Files.write(archivo.toPath(), lines, StandardCharsets.UTF_8);
    }

    public List<Suministro> cargar() throws IOException {
        File archivo = RutasPersistencia.archivoInventario();
        List<Suministro> suministros = new ArrayList<>();

        if (!archivo.exists()) {
            return suministros;
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
            if (!"SUMINISTRO".equals(partes[0])) {
                continue;
            }

            String id = partes[1];
            String nombre = partes[2];
            int stock = Integer.parseInt(partes[3]);
            int stockMinimo = Integer.parseInt(partes[4]);
            suministros.add(new Suministro(id, nombre, stock, stockMinimo));
        }

        return suministros;
    }
}

