package GUI.panel;

import GUI.NavigationController;
import GUI.ValidationUI;
import Kernel.Asignacion;
import Kernel.Brigadista;
import Kernel.Incidente;
import Kernel.NoEncontradoException;
import Kernel.Residente;
import Kernel.SistemaEmergencias;
import Kernel.Usuario;
import Kernel.ValidacionException;
import Kernel.Validador;
import PersistenceLayer.AsignacionFileStore;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IncidentesPanel extends JPanel {

    private static final String KEY_BITACORA = "bitacora";

    private final SistemaEmergencias sistema;
    private final NavigationController navigation;
    private final String rolUsuario;
    private final Validador validador;

    private final DefaultListModel<Incidente> incidentesModel;
    private final JList<Incidente> listaIncidentes;
    private final JTextArea detalleArea;
    private final JTextArea salidaArea;

    private JTextField txtTipo;
    private JTextField txtSeveridad;
    private JTextField txtPrioridad;
    private JTextField txtUbicacion;
    private JTextField txtDescripcion;

    private JTextField txtBrigadistaId;
    private final JComboBox<String> cmbFiltro;

    public IncidentesPanel(SistemaEmergencias sistema, String rolUsuario, NavigationController navigation) {
        this.sistema = sistema;
        this.navigation = navigation;
        this.rolUsuario = (rolUsuario == null || rolUsuario.trim().isEmpty())
                ? Usuario.ROL_RESIDENTE
                : rolUsuario;
        this.validador = new Validador();

        this.txtTipo = new JTextField();
        this.txtSeveridad = new JTextField();
        this.txtPrioridad = new JTextField();
        this.txtUbicacion = new JTextField();
        this.txtDescripcion = new JTextField();
        this.txtBrigadistaId = new JTextField();

        ValidationUI.configureField(txtTipo);
        ValidationUI.configureField(txtSeveridad);
        ValidationUI.configureField(txtPrioridad);
        ValidationUI.configureField(txtUbicacion);
        ValidationUI.configureField(txtDescripcion);
        ValidationUI.configureField(txtBrigadistaId);

        ValidationUI.attachAutoClear(txtTipo);
        ValidationUI.attachAutoClear(txtSeveridad);
        ValidationUI.attachAutoClear(txtPrioridad);
        ValidationUI.attachAutoClear(txtUbicacion);
        ValidationUI.attachAutoClear(txtDescripcion);
        ValidationUI.attachAutoClear(txtBrigadistaId);

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Incidentes");
        header.add(titulo, BorderLayout.WEST);

        cmbFiltro = new JComboBox<>(new String[]{"Todos", "Pendientes"});
        cmbFiltro.addActionListener(e -> cargarLista());
        if (esCoordinador()) {
            header.add(cmbFiltro, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        incidentesModel = new DefaultListModel<>();
        listaIncidentes = new JList<>(incidentesModel);
        listaIncidentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaIncidentes.addListSelectionListener(e -> actualizarDetalle());
        listaIncidentes.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(formatoIncidente(value));
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });

        detalleArea = new JTextArea(6, 40);
        detalleArea.setEditable(false);
        detalleArea.setBorder(BorderFactory.createTitledBorder("Detalle"));

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(listaIncidentes), BorderLayout.CENTER);
        listPanel.add(detalleArea, BorderLayout.SOUTH);

        center.add(listPanel, BorderLayout.CENTER);
        center.add(crearPanelAcciones(), BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        salidaArea = new JTextArea(3, 40);
        salidaArea.setEditable(false);
        add(new JScrollPane(salidaArea), BorderLayout.SOUTH);

        cargarLista();
    }

    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton btnVerBitacora = new JButton("Ver bitacora");
        btnVerBitacora.addActionListener(e -> abrirBitacora());
        panel.add(btnVerBitacora, gbc);

        gbc.gridy++;
        JButton btnActualizar = new JButton("Actualizar lista");
        btnActualizar.addActionListener(e -> cargarLista());
        panel.add(btnActualizar, gbc);

        if (esResidente()) {
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints fbc = new GridBagConstraints();
            fbc.insets = new Insets(4, 4, 4, 4);
            fbc.fill = GridBagConstraints.HORIZONTAL;

            addField(form, fbc, 0, "Tipo:", txtTipo);
            addField(form, fbc, 1, "Severidad:", txtSeveridad);
            addField(form, fbc, 2, "Prioridad:", txtPrioridad);
            addField(form, fbc, 3, "Ubicacion:", txtUbicacion);
            addField(form, fbc, 4, "Descripcion:", txtDescripcion);

            JButton btnReportar = new JButton("Crear incidente");
            btnReportar.addActionListener(e -> reportarIncidente());
            fbc.gridx = 0;
            fbc.gridy = 5;
            fbc.gridwidth = 2;
            form.add(btnReportar, fbc);

            gbc.gridy++;
            gbc.gridwidth = 1;
            panel.add(form, gbc);
        }

        if (esCoordinador()) {
            JPanel asignacion = new JPanel(new GridBagLayout());
            GridBagConstraints abc = new GridBagConstraints();
            abc.insets = new Insets(4, 4, 4, 4);
            abc.fill = GridBagConstraints.HORIZONTAL;

            addField(asignacion, abc, 0, "Id brigadista:", txtBrigadistaId);

            JButton btnAsignar = new JButton("Asignar brigadista");
            btnAsignar.addActionListener(e -> asignarBrigadista());
            abc.gridx = 0;
            abc.gridy = 1;
            abc.gridwidth = 2;
            asignacion.add(btnAsignar, abc);

            gbc.gridy++;
            panel.add(asignacion, gbc);
        }

        return panel;
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private void cargarLista() {
        incidentesModel.clear();
        List<Incidente> lista = obtenerIncidentes();
        for (Incidente incidente : lista) {
            incidentesModel.addElement(incidente);
        }
        if (!incidentesModel.isEmpty()) {
            listaIncidentes.setSelectedIndex(0);
        }
    }

    private List<Incidente> obtenerIncidentes() {
        List<Incidente> resultado = new ArrayList<>();
        if (esResidente()) {
            Residente residente = sistema.getResidenteActual();
            if (residente == null) {
                return resultado;
            }
            for (int i = 0; i < residente.getCantidadIncidentes(); i++) {
                Incidente incidente = residente.getIncidentes()[i];
                if (incidente != null) {
                    resultado.add(incidente);
                }
            }
            return resultado;
        }

        int total = sistema.getCantidadIncidentes();
        for (int i = 0; i < total; i++) {
            Incidente incidente = sistema.getIncidentes()[i];
            if (incidente == null) {
                continue;
            }
            if (esCoordinador() && "Pendientes".equals(cmbFiltro.getSelectedItem())) {
                if (!Incidente.ESTADO_PENDIENTE.equals(incidente.getEstado())) {
                    continue;
                }
            }
            resultado.add(incidente);
        }
        return resultado;
    }

    private void actualizarDetalle() {
        Incidente incidente = listaIncidentes.getSelectedValue();
        if (incidente == null) {
            detalleArea.setText("");
            return;
        }
        StringBuilder detalle = new StringBuilder();
        detalle.append("Id: ").append(incidente.getId()).append("\n");
        detalle.append("Tipo: ").append(incidente.getTipo()).append("\n");
        detalle.append("Estado: ").append(incidente.getEstado()).append("\n");
        detalle.append("Prioridad: ").append(incidente.getPrioridad()).append("\n");
        detalle.append("Ubicacion: ").append(incidente.getUbicacion()).append("\n");
        detalle.append("Descripcion: ").append(incidente.getDescripcion()).append("\n");
        detalle.append("Fecha: ").append(incidente.getFechaCreacion());
        detalleArea.setText(detalle.toString());
    }

    private void reportarIncidente() {
        String tipo = validarTextoNoVacio(txtTipo, "tipo");
        if (tipo == null) {
            return;
        }
        String severidad = validarTextoNoVacio(txtSeveridad, "severidad");
        if (severidad == null) {
            return;
        }
        String prioridad = validarTextoNoVacio(txtPrioridad, "prioridad");
        if (prioridad == null) {
            return;
        }
        String ubicacion = validarTextoNoVacio(txtUbicacion, "ubicacion");
        if (ubicacion == null) {
            return;
        }
        String descripcion = validarTextoNoVacio(txtDescripcion, "descripcion");
        if (descripcion == null) {
            return;
        }

        Incidente incidente = sistema.reportarIncidente(tipo, severidad, prioridad, ubicacion, descripcion);
        salidaArea.setText("Incidente reportado con id " + incidente.getId());
        limpiarFormulario();
        cargarLista();
    }

    private void asignarBrigadista() {
        Incidente incidente = listaIncidentes.getSelectedValue();
        if (incidente == null) {
            salidaArea.setText("Seleccione un incidente.");
            return;
        }
        Integer idBrigadista = validarEnteroPositivo(txtBrigadistaId, "id brigadista");
        if (idBrigadista == null) {
            return;
        }

        Brigadista brigadista = sistema.buscarBrigadistaPorId(idBrigadista);
        if (brigadista == null) {
            salidaArea.setText("No se encontro brigadista con id " + idBrigadista);
            return;
        }
        if (!brigadista.isDisponible()) {
            salidaArea.setText("Brigadista no disponible.");
            return;
        }

        AsignacionFileStore store = new AsignacionFileStore();
        List<Asignacion> asignaciones = new ArrayList<>();
        try {
            java.util.Map<Integer, Brigadista> mapa = new java.util.HashMap<>();
            for (Brigadista item : sistema.getBrigadistas()) {
                mapa.put(item.getId(), item);
            }
            asignaciones.addAll(store.cargar(mapa));
        } catch (IOException e) {
            salidaArea.setText("No se pudieron cargar asignaciones: " + e.getMessage());
            return;
        }

        if (existeAsignacion(asignaciones, incidente.getId(), brigadista.getId())) {
            salidaArea.setText("La asignacion ya existe.");
            return;
        }

        asignaciones.add(new Asignacion(brigadista, incidente.getId()));
        try {
            store.guardar(asignaciones);
        } catch (IOException e) {
            salidaArea.setText("No se pudo guardar la asignacion: " + e.getMessage());
            return;
        }
        sistema.actualizarEstadoIncidente(incidente.getId(), Incidente.ESTADO_ASIGNADO);
        salidaArea.setText("Brigadista asignado al incidente " + incidente.getId());
        cargarLista();
    }

    private boolean existeAsignacion(List<Asignacion> asignaciones, int idIncidente, int idBrigadista) {
        for (Asignacion asignacion : asignaciones) {
            if (asignacion.getIdIncidente() == idIncidente
                    && asignacion.getBrigadista().getId() == idBrigadista) {
                return true;
            }
        }
        return false;
    }

    private void abrirBitacora() {
        Incidente incidente = listaIncidentes.getSelectedValue();
        if (incidente == null) {
            salidaArea.setText("Seleccione un incidente para ver bitacoras.");
            return;
        }
        if (navigation != null) {
            JPanel panel = navigation.getPanel(KEY_BITACORA);
            if (panel instanceof BitacoraPanel) {
                ((BitacoraPanel) panel).setIncidenteId(incidente.getId());
            }
            navigation.show(KEY_BITACORA);
        }
    }

    private String formatoIncidente(Incidente incidente) {
        return iconoEstado(incidente.getEstado()) + " #" + incidente.getId() + " | "
                + incidente.getTipo() + " | " + incidente.getPrioridad() + " | "
                + incidente.getEstado() + " | " + incidente.getUbicacion();
    }

    private String iconoEstado(String estado) {
        if (Incidente.ESTADO_PENDIENTE.equals(estado)) {
            return "[P]";
        }
        if (Incidente.ESTADO_ASIGNADO.equals(estado)) {
            return "[A]";
        }
        if (Incidente.ESTADO_EN_CAMINO.equals(estado)) {
            return "[C]";
        }
        if (Incidente.ESTADO_EN_INTERVENCION.equals(estado)) {
            return "[I]";
        }
        if (Incidente.ESTADO_FINALIZADO.equals(estado)) {
            return "[F]";
        }
        return "[X]";
    }

    private void limpiarFormulario() {
        txtTipo.setText("");
        txtSeveridad.setText("");
        txtPrioridad.setText("");
        txtUbicacion.setText("");
        txtDescripcion.setText("");
    }

    private boolean esResidente() {
        return Usuario.ROL_RESIDENTE.equalsIgnoreCase(rolUsuario);
    }

    private boolean esCoordinador() {
        return Usuario.ROL_COORDINADOR.equalsIgnoreCase(rolUsuario);
    }

    public void refrescarLista() {
        cargarLista();
    }

    private String validarTextoNoVacio(JTextField field, String campo) {
        try {
            return validador.validarTextoNoVacio(field.getText(), campo);
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, field, ex.getMessage());
            return null;
        }
    }

    private Integer validarEnteroPositivo(JTextField field, String campo) {
        try {
            int valor = validador.convertirEntero(field.getText(), campo);
            validador.validarEnteroPositivo(valor, campo);
            return valor;
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, field, ex.getMessage());
            return null;
        }
    }
}
