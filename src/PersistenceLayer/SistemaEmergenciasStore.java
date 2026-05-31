package PersistenceLayer;

import Kernel.Bitacora;
import Kernel.Incidente;
import Kernel.Residente;
import Kernel.SistemaEmergencias;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaEmergenciasStore {

    public static class EstadoSistema {
        private final Incidente[] incidentes;
        private final int cantidadIncidentes;
        private final Residente residenteActual;
        private final int contadorIncidentes;
        private final int contadorBitacoras;

        public EstadoSistema(Incidente[] incidentes, int cantidadIncidentes, Residente residenteActual,
                             int contadorIncidentes, int contadorBitacoras) {
            this.incidentes = incidentes;
            this.cantidadIncidentes = cantidadIncidentes;
            this.residenteActual = residenteActual;
            this.contadorIncidentes = contadorIncidentes;
            this.contadorBitacoras = contadorBitacoras;
        }

        public Incidente[] getIncidentes() {
            return incidentes;
        }

        public int getCantidadIncidentes() {
            return cantidadIncidentes;
        }

        public Residente getResidenteActual() {
            return residenteActual;
        }

        public int getContadorIncidentes() {
            return contadorIncidentes;
        }

        public int getContadorBitacoras() {
            return contadorBitacoras;
        }
    }

    private final IncidenteFileStore incidenteStore = new IncidenteFileStore();
    private final ResidenteFileStore residenteStore = new ResidenteFileStore();

    public void guardar(SistemaEmergencias sistema) throws IOException {
        incidenteStore.guardar(sistema.getIncidentes(), sistema.getCantidadIncidentes());
        residenteStore.guardar(sistema.getResidenteActual());
    }

    public EstadoSistema cargar() throws IOException {
        List<Incidente> incidentes = incidenteStore.cargar();
        Map<Integer, Incidente> incidentesPorId = new HashMap<>();

        int maxIncidenteId = 0;
        int maxBitacoraId = 0;
        for (Incidente incidente : incidentes) {
            incidentesPorId.put(incidente.getId(), incidente);
            if (incidente.getId() > maxIncidenteId) {
                maxIncidenteId = incidente.getId();
            }
            for (int i = 0; i < incidente.getCantidadBitacoras(); i++) {
                Bitacora bitacora = incidente.getBitacoras()[i];
                if (bitacora != null && bitacora.getId() > maxBitacoraId) {
                    maxBitacoraId = bitacora.getId();
                }
            }
        }

        Residente residente = residenteStore.cargar(incidentesPorId);

        if (incidentes.isEmpty() && residente == null) {
            return null;
        }

        int capacidad = Math.max(100, incidentes.size());
        Incidente[] arreglo = new Incidente[capacidad];
        for (int i = 0; i < incidentes.size(); i++) {
            arreglo[i] = incidentes.get(i);
        }

        return new EstadoSistema(arreglo, incidentes.size(), residente, maxIncidenteId, maxBitacoraId);
    }
}
