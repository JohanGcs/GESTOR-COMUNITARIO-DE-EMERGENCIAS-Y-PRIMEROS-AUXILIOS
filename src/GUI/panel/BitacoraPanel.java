package GUI.panel;

import GUI.NavigationController;
import GUI.ValidationUI;
import Kernel.Bitacora;
import Kernel.Incidente;
import Kernel.NoEncontradoException;
import Kernel.SistemaEmergencias;
import Kernel.Usuario;
import Kernel.ValidacionException;
import Kernel.Validador;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class BitacoraPanel extends JPanel {

    private static final String KEY_INCIDENTES = "incidentes";

    private final SistemaEmergencias sistema;
    private final NavigationController navigation;
    private final String rolUsuario;
    private final Validador validador;

    private final JLabel lblEstadoGrande;
    private final JLabel lblInfo;
    private final DefaultListModel<String> bitacorasModel;
    private final JList<String> listaBitacoras;
    private final JTextArea salidaArea;

    private final JTextField txtIncidenteId;
    private final JTextField txtAccion;
    private final JTextField txtDetalle;
    private int incidenteSeleccionado;

    public BitacoraPanel(SistemaEmergencias sistema, String rolUsuario, NavigationController navigation) {
        this.sistema = sistema;
        this.navigation = navigation;
        this.rolUsuario = (rolUsuario == null || rolUsuario.trim().isEmpty())
                ? Usuario.ROL_RESIDENTE
                : rolUsuario;
        this.validador = new Validador();
        this.incidenteSeleccionado = -1;

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        lblEstadoGrande = new JLabel("ESTADO", JLabel.CENTER);
        lblEstadoGrande.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblEstadoGrande.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        header.add(lblEstadoGrande, BorderLayout.NORTH);

        lblInfo = new JLabel("Seleccione un incidente", JLabel.CENTER);
        header.add(lblInfo, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        bitacorasModel = new DefaultListModel<>();
        listaBitacoras = new JList<>(bitacorasModel);
        center.add(new JScrollPane(listaBitacoras), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIncidenteId = new JTextField();
        txtAccion = new JTextField();
        txtDetalle = new JTextField();

        ValidationUI.configureField(txtIncidenteId);
        ValidationUI.configureField(txtAccion);
        ValidationUI.configureField(txtDetalle);

        ValidationUI.attachAutoClear(txtIncidenteId);
        ValidationUI.attachAutoClear(txtAccion);
        ValidationUI.attachAutoClear(txtDetalle);

        addField(form, gbc, 0, "Id incidente:", txtIncidenteId);
        addField(form, gbc, 1, "Accion:", txtAccion);
        addField(form, gbc, 2, "Detalle:", txtDetalle);

        JButton btnAgregar = new JButton("Agregar entrada");
        btnAgregar.addActionListener(e -> registrarBitacora());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(btnAgregar, gbc);

        if (esBrigadista()) {
            JButton btnEnCamino = new JButton("En camino");
            btnEnCamino.addActionListener(e -> cambiarEstado(Incidente.ESTADO_EN_CAMINO));
            JButton btnIntervencion = new JButton("En intervencion");
            btnIntervencion.addActionListener(e -> cambiarEstado(Incidente.ESTADO_EN_INTERVENCION));
            JButton btnFinalizado = new JButton("Finalizado");
            btnFinalizado.addActionListener(e -> cambiarEstado(Incidente.ESTADO_FINALIZADO));

            gbc.gridy = 4;
            form.add(btnEnCamino, gbc);
            gbc.gridy = 5;
            form.add(btnIntervencion, gbc);
            gbc.gridy = 6;
            form.add(btnFinalizado, gbc);
        }

        center.add(form, BorderLayout.EAST);
        add(center, BorderLayout.CENTER);

        salidaArea = new JTextArea(3, 40);
        salidaArea.setEditable(false);
        add(new JScrollPane(salidaArea), BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    public void setIncidenteId(int idIncidente) {
        this.incidenteSeleccionado = idIncidente;
        txtIncidenteId.setText(String.valueOf(idIncidente));
        cargarIncidente();
    }

    private void cargarIncidente() {
        Integer id = obtenerIdIncidente();
        if (id == null) {
            return;
        }
        try {
            Incidente incidente = sistema.obtenerIncidentePorId(id);
            actualizarDetalle(incidente);
            cargarBitacoras(incidente);
        } catch (NoEncontradoException ex) {
            salidaArea.setText(ex.getMessage());
        }
    }

    private void actualizarDetalle(Incidente incidente) {
        String estado = incidente.getEstado();
        lblEstadoGrande.setText(iconoEstado(estado) + " " + estado);
        String info = "#" + incidente.getId() + " | " + incidente.getTipo() + " | "
                + incidente.getPrioridad() + " | " + incidente.getUbicacion();
        lblInfo.setText(info);
    }

    private void cargarBitacoras(Incidente incidente) {
        bitacorasModel.clear();
        if (incidente.getCantidadBitacoras() == 0) {
            bitacorasModel.addElement("Sin bitacoras registradas.");
            return;
        }
        Bitacora[] bitacoras = incidente.getBitacoras();
        for (int i = 0; i < incidente.getCantidadBitacoras(); i++) {
            if (bitacoras[i] != null) {
                bitacorasModel.addElement(bitacoras[i].resumen());
            }
        }
    }

    private void registrarBitacora() {
        Integer id = obtenerIdIncidente();
        if (id == null) {
            return;
        }
        String accion = validarTextoNoVacio(txtAccion, "accion");
        if (accion == null) {
            return;
        }
        String detalle = validarTextoNoVacio(txtDetalle, "detalle");
        if (detalle == null) {
            return;
        }

        try {
            sistema.registrarBitacora(id, accion, detalle);
            txtAccion.setText("");
            txtDetalle.setText("");
            salidaArea.setText("Entrada agregada.");
            cargarIncidente();
            refrescarListas();
        } catch (NoEncontradoException ex) {
            salidaArea.setText(ex.getMessage());
        }
    }

    private void cambiarEstado(String nuevoEstado) {
        Integer id = obtenerIdIncidente();
        if (id == null) {
            return;
        }
        try {
            sistema.actualizarEstadoIncidente(id, nuevoEstado);
            salidaArea.setText("Estado actualizado.");
            cargarIncidente();
            refrescarListas();
        } catch (NoEncontradoException ex) {
            salidaArea.setText(ex.getMessage());
        }
    }

    private Integer obtenerIdIncidente() {
        String texto = txtIncidenteId.getText();
        if ((texto == null || texto.trim().isEmpty()) && incidenteSeleccionado > 0) {
            return incidenteSeleccionado;
        }
        try {
            int id = validador.convertirEntero(texto, "id incidente");
            validador.validarEnteroPositivo(id, "id incidente");
            incidenteSeleccionado = id;
            return id;
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, txtIncidenteId, ex.getMessage());
            return null;
        }
    }

    private String validarTextoNoVacio(JTextField field, String campo) {
        try {
            return validador.validarTextoNoVacio(field.getText(), campo);
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, field, ex.getMessage());
            return null;
        }
    }

    private void refrescarListas() {
        if (navigation == null) {
            return;
        }
        JPanel panel = navigation.getPanel(KEY_INCIDENTES);
        if (panel instanceof IncidentesPanel) {
            ((IncidentesPanel) panel).refrescarLista();
        }
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

    private boolean esBrigadista() {
        return Usuario.ROL_BRIGADISTA.equalsIgnoreCase(rolUsuario);
    }
}
