package Kernel;

import PersistenceLayer.SistemaEmergenciasStore;
import PersistenceLayer.UsuarioFileStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase SistemaEmergencias que gestiona incidentes, bitacoras y guias.
 */
public class SistemaEmergencias {

    // Atributos
    private Residente residenteActual;
    private Usuario usuarioActual;
    private List<Usuario> usuarios;
    private int contadorUsuarios;

    private Incidente[] incidentes;
    private int cantidadIncidentes;
    private int contadorIncidentes;

    private Guia[] guias;
    private int cantidadGuias;
    private int contadorBitacoras;

    // Constructor
    public SistemaEmergencias() {
        // Inicializa arreglos y contadores.
        this.incidentes = new Incidente[100];
        this.cantidadIncidentes = 0;
        this.contadorIncidentes = 0;
        this.guias = new Guia[20];
        this.cantidadGuias = 0;
        this.contadorBitacoras = 0;
        // Carga guias predefinidas.
        cargarGuiasIniciales();
        restaurarDesdePersistencia();
        cargarUsuarios();
    }

    public Residente registrarResidente(String nombre, String correo, String telefono) {
        try {
            Usuario usuario = registrarUsuario(nombre, correo, telefono, "", Usuario.ROL_RESIDENTE);
            return (Residente) usuario;
        } catch (ValidacionException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public Usuario registrarUsuario(String nombre, String correo, String telefono, String password, String rol) throws ValidacionException {
        validarRol(rol);
        if (buscarUsuarioPorCorreo(correo) != null) {
            throw new ValidacionException("Ya existe una cuenta con ese correo.");
        }

        int id = generarIdUsuario();
        Usuario usuario;
        if (Usuario.ROL_RESIDENTE.equalsIgnoreCase(rol)) {
            usuario = new Residente(id, nombre, correo, telefono, password);
        } else if (Usuario.ROL_BRIGADISTA.equalsIgnoreCase(rol)) {
            usuario = new Brigadista(id, nombre, correo, telefono, password);
        } else {
            usuario = new Coordinador(id, nombre, correo, telefono, password);
        }

        usuarios.add(usuario);
        usuarioActual = usuario;
        if (usuario instanceof Residente) {
            residenteActual = (Residente) usuario;
        } else {
            residenteActual = null;
        }

        guardarUsuarios();
        return usuario;
    }

    public Usuario login(String correo, String password) throws ValidacionException {
        Usuario usuario = buscarUsuarioPorCorreo(correo);
        if (usuario == null || !usuario.getPasswordHash().equals(password)) {
            throw new ValidacionException("Credenciales invalidas.");
        }
        usuarioActual = usuario;
        if (usuario instanceof Residente) {
            residenteActual = (Residente) usuario;
        } else {
            residenteActual = null;
        }
        return usuario;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Residente getResidenteActual() {
        return residenteActual;
    }

    public String getRolUsuarioActual() {
        return usuarioActual == null ? "" : usuarioActual.getRol();
    }

    public List<Brigadista> getBrigadistas() {
        List<Brigadista> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Brigadista) {
                resultado.add((Brigadista) usuario);
            }
        }
        return resultado;
    }

    public Brigadista buscarBrigadistaPorId(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Brigadista && usuario.getId() == id) {
                return (Brigadista) usuario;
            }
        }
        return null;
    }

    public Brigadista cambiarDisponibilidadBrigadista(int id, boolean disponible) throws ValidacionException {
        Brigadista brigadista = buscarBrigadistaPorId(id);
        if (brigadista == null) {
            throw new ValidacionException("No se encontro brigadista con id " + id);
        }
        brigadista.cambiarDisponibilidad(disponible);
        guardarUsuarios();
        return brigadista;
    }

    public void persistirUsuarios() {
        guardarUsuarios();
    }

    public Incidente[] getIncidentes() {
        return incidentes;
    }

    public int getCantidadIncidentes() {
        return cantidadIncidentes;
    }

    public int getContadorIncidentes() {
        return contadorIncidentes;
    }

    public int getContadorBitacoras() {
        return contadorBitacoras;
    }

    public Incidente reportarIncidente(String tipo, String severidad, String prioridad, String ubicacion, String descripcion) throws ValidacionException {
        // Valida que exista un residente activo.
        if (residenteActual == null) {
            throw new ValidacionException("Debe registrar un residente antes de reportar.");
        }

        // Crea el incidente con estado inicial.
        contadorIncidentes = contadorIncidentes + 1;
        Incidente incidente = new Incidente(
                contadorIncidentes,
                tipo,
                severidad,
                prioridad,
                ubicacion,
                descripcion,
                Incidente.ESTADO_PENDIENTE,
                FechaUtil.ahora()
        );

        // Guarda el incidente en el arreglo principal.
        if (cantidadIncidentes < incidentes.length) {
            incidentes[cantidadIncidentes] = incidente;
            cantidadIncidentes = cantidadIncidentes + 1;
        }

        // Asocia el incidente al residente.
        residenteActual.agregarIncidente(incidente);
        guardarEstado();
        return incidente;
    }

    public Bitacora registrarBitacora(int idIncidente, String accion, String detalle) throws NoEncontradoException {
        // Busca el incidente y agrega una bitacora.
        Incidente incidente = buscarIncidentePorId(idIncidente);
        contadorBitacoras = contadorBitacoras + 1;
        Bitacora bitacora = new Bitacora(contadorBitacoras, accion, detalle, FechaUtil.ahora());
        incidente.agregarBitacora(bitacora);
        guardarEstado();
        return bitacora;
    }

    public void actualizarEstadoIncidente(int idIncidente, String nuevoEstado) throws NoEncontradoException {
        Incidente incidente = buscarIncidentePorId(idIncidente);
        incidente.actualizarEstado(nuevoEstado);
        guardarEstado();
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

    public String listarBitacoras(int idIncidente) throws NoEncontradoException {
        // Lista bitacoras de un incidente especifico.
        Incidente incidente = buscarIncidentePorId(idIncidente);
        if (incidente.getCantidadBitacoras() == 0) {
            return "Sin bitacoras registradas.";
        }

        String salida = "";
        for (int i = 0; i < incidente.getCantidadBitacoras(); i++) {
            salida = salida + "- " + incidente.getBitacoras()[i].resumen() + "\n";
        }
        return salida;
    }

    public String buscarGuiaPorTipo(String tipo) throws NoEncontradoException {
        // Busca una guia por tipo de emergencia.
        for (int i = 0; i < cantidadGuias; i++) {
            if (guias[i].getTipoEmergencia().equalsIgnoreCase(tipo)) {
                return guias[i].resumen();
            }
        }
        throw new NoEncontradoException("No se encontro guia para el tipo: " + tipo);
    }

    /** Devuelve los tipos de emergencia que tienen guia disponible. */
    public String[] getTiposConGuia() {
        String[] tipos = new String[cantidadGuias];
        for (int i = 0; i < cantidadGuias; i++) {
            tipos[i] = guias[i].getTipoEmergencia();
        }
        return tipos;
    }

    public Incidente obtenerIncidentePorId(int idIncidente) throws NoEncontradoException {
        return buscarIncidentePorId(idIncidente);
    }

    private Incidente buscarIncidentePorId(int idIncidente) throws NoEncontradoException {
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
        agregarGuia(new Guia(1, "incendio", "Incendio", "Mantenga la calma, evacue y llame a emergencias."));
        agregarGuia(new Guia(2, "caida", "Caida", "Inmovilice y espere ayuda."));
        agregarGuia(new Guia(3, "cortadura", "Cortadura", "Presione la herida y limpie con cuidado."));
        agregarGuia(new Guia(4, "sismo", "Sismo", "Ubique un lugar seguro y siga el protocolo."));
    }

    private void agregarGuia(Guia guia) {
        // Agrega una guia al arreglo si hay espacio.
        if (cantidadGuias < guias.length) {
            guias[cantidadGuias] = guia;
            cantidadGuias = cantidadGuias + 1;
        }
    }

    private void restaurarDesdePersistencia() {
        SistemaEmergenciasStore store = new SistemaEmergenciasStore();
        try {
            SistemaEmergenciasStore.EstadoSistema estado = store.cargar();
            if (estado == null) {
                return;
            }
            if (estado.getIncidentes() != null) {
                this.incidentes = estado.getIncidentes();
                this.cantidadIncidentes = estado.getCantidadIncidentes();
            }
            this.contadorIncidentes = estado.getContadorIncidentes();
            this.contadorBitacoras = estado.getContadorBitacoras();
            this.residenteActual = estado.getResidenteActual();
        } catch (IOException e) {
            System.err.println("No se pudo cargar la informacion guardada: " + e.getMessage());
        }
    }

    private void guardarEstado() {
        SistemaEmergenciasStore store = new SistemaEmergenciasStore();
        try {
            store.guardar(this);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la informacion: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        UsuarioFileStore store = new UsuarioFileStore();
        usuarios = new ArrayList<>();
        try {
            usuarios.addAll(store.cargar());
        } catch (IOException e) {
            System.err.println("No se pudo cargar usuarios: " + e.getMessage());
        }
        contadorUsuarios = 0;
        for (Usuario usuario : usuarios) {
            if (usuario.getId() > contadorUsuarios) {
                contadorUsuarios = usuario.getId();
            }
        }
    }

    private void guardarUsuarios() {
        UsuarioFileStore store = new UsuarioFileStore();
        try {
            store.guardar(usuarios);
        } catch (IOException e) {
            System.err.println("No se pudo guardar usuarios: " + e.getMessage());
        }
    }

    private Usuario buscarUsuarioPorCorreo(String correo) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                return usuario;
            }
        }
        return null;
    }

    private int generarIdUsuario() {
        contadorUsuarios = contadorUsuarios + 1;
        return contadorUsuarios;
    }

    private void validarRol(String rol) throws ValidacionException {
        if (!Usuario.ROL_RESIDENTE.equalsIgnoreCase(rol)
                && !Usuario.ROL_BRIGADISTA.equalsIgnoreCase(rol)
                && !Usuario.ROL_COORDINADOR.equalsIgnoreCase(rol)) {
            throw new ValidacionException("Rol no valido.");
        }
    }
}
