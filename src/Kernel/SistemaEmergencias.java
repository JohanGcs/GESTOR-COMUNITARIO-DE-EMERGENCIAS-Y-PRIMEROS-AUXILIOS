package Kernel;

import POOLabFinal.Bitacora;
import POOLabFinal.FechaUtil;
import POOLabFinal.Guia;
import POOLabFinal.Incidente;
import POOLabFinal.NoEncontradoException;
import POOLabFinal.Residente;
import POOLabFinal.ValidacionException;

/**
 * Clase SistemaEmergencias que gestiona incidentes, bitacoras y guias.
 */
public class SistemaEmergencias {

    // Atributos
    private POOLabFinal.Residente residenteActual;
    private POOLabFinal.Incidente[] incidentes;
    private int cantidadIncidentes;
    private int contadorIncidentes;

    private POOLabFinal.Guia[] guias;
    private int cantidadGuias;
    private int contadorBitacoras;

    // Constructor
    public SistemaEmergencias() {
        // Inicializa arreglos y contadores.
        this.incidentes = new POOLabFinal.Incidente[100];
        this.cantidadIncidentes = 0;
        this.contadorIncidentes = 0;
        this.guias = new POOLabFinal.Guia[20];
        this.cantidadGuias = 0;
        this.contadorBitacoras = 0;
        // Carga guias predefinidas.
        cargarGuiasIniciales();
    }

    public POOLabFinal.Residente registrarResidente(String nombre, String correo, String telefono) {
        // Registra un residente como usuario activo.
        this.residenteActual = new POOLabFinal.Residente(1, nombre, correo, telefono, "");
        return residenteActual;
    }

    public Residente getResidenteActual() {
        return residenteActual;
    }

    public POOLabFinal.Incidente reportarIncidente(String tipo, String severidad, String prioridad, String ubicacion, String descripcion) throws ValidacionException {
        // Valida que exista un residente activo.
        if (residenteActual == null) {
            throw new ValidacionException("Debe registrar un residente antes de reportar.");
        }

        // Crea el incidente con estado inicial.
        contadorIncidentes = contadorIncidentes + 1;
        POOLabFinal.Incidente incidente = new POOLabFinal.Incidente(
                contadorIncidentes,
                tipo,
                severidad,
                prioridad,
                ubicacion,
                descripcion,
                POOLabFinal.Incidente.ESTADO_PENDIENTE,
                POOLabFinal.FechaUtil.ahora()
        );

        // Guarda el incidente en el arreglo principal.
        if (cantidadIncidentes < incidentes.length) {
            incidentes[cantidadIncidentes] = incidente;
            cantidadIncidentes = cantidadIncidentes + 1;
        }

        // Asocia el incidente al residente.
        residenteActual.agregarIncidente(incidente);
        return incidente;
    }

    public POOLabFinal.Bitacora registrarBitacora(int idIncidente, String accion, String detalle) throws POOLabFinal.NoEncontradoException {
        // Busca el incidente y agrega una bitacora.
        POOLabFinal.Incidente incidente = buscarIncidentePorId(idIncidente);
        contadorBitacoras = contadorBitacoras + 1;
        POOLabFinal.Bitacora bitacora = new Bitacora(contadorBitacoras, accion, detalle, FechaUtil.ahora());
        incidente.agregarBitacora(bitacora);
        return bitacora;
    }

    public String listarIncidentes() {
        // Devuelve un listado con formato simple.
        if (cantidadIncidentes == 0) {
            return "Sin incidentes registrados.";
        }

        String salida = "";
        for (int i = 0; i < cantidadIncidentes; i++) {
            salida = salida + "- " + incidentes[i].resumen() + "\n";
        }
        return salida;
    }

    public String listarBitacoras(int idIncidente) throws POOLabFinal.NoEncontradoException {
        // Lista bitacoras de un incidente especifico.
        POOLabFinal.Incidente incidente = buscarIncidentePorId(idIncidente);
        if (incidente.getCantidadBitacoras() == 0) {
            return "Sin bitacoras registradas.";
        }

        String salida = "";
        for (int i = 0; i < incidente.getCantidadBitacoras(); i++) {
            salida = salida + "- " + incidente.getBitacoras()[i].resumen() + "\n";
        }
        return salida;
    }

    public String buscarGuiaPorTipo(String tipo) throws POOLabFinal.NoEncontradoException {
        // Busca una guia por tipo de emergencia.
        for (int i = 0; i < cantidadGuias; i++) {
            if (guias[i].getTipoEmergencia().equalsIgnoreCase(tipo)) {
                return guias[i].resumen();
            }
        }
        throw new POOLabFinal.NoEncontradoException("No se encontro guia para el tipo: " + tipo);
    }

    private Incidente buscarIncidentePorId(int idIncidente) throws POOLabFinal.NoEncontradoException {
        // Busca un incidente por identificador.
        for (int i = 0; i < cantidadIncidentes; i++) {
            if (incidentes[i].getId() == idIncidente) {
                return incidentes[i];
            }
        }
        throw new NoEncontradoException("No se encontro incidente con id: " + idIncidente);
    }

    private void cargarGuiasIniciales() {
        // Carga guias basicas predefinidas.
        agregarGuia(new POOLabFinal.Guia(1, "incendio", "Incendio", "Mantenga la calma, evacue y llame a emergencias."));
        agregarGuia(new POOLabFinal.Guia(2, "caida", "Caida", "Inmovilice y espere ayuda."));
        agregarGuia(new POOLabFinal.Guia(3, "cortadura", "Cortadura", "Presione la herida y limpie con cuidado."));
        agregarGuia(new POOLabFinal.Guia(4, "sismo", "Sismo", "Ubique un lugar seguro y siga el protocolo."));
    }

    private void agregarGuia(Guia guia) {
        // Agrega una guia al arreglo si hay espacio.
        if (cantidadGuias < guias.length) {
            guias[cantidadGuias] = guia;
            cantidadGuias = cantidadGuias + 1;
        }
    }
}
