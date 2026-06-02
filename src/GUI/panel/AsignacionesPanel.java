package GUI.panel;

import GUI.NavigationController;
import Kernel.Asignacion;
import Kernel.Brigadista;
import Kernel.SistemaEmergencias;
import Kernel.Usuario;
import PersistenceLayer.AsignacionFileStore;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsignacionesPanel extends JPanel {

    private static final String KEY_BITACORA = "bitacora";

    private final SistemaEmergencias sistema;
    private final AsignacionFileStore store;
    private final NavigationController navigation;

    private final DefaultListModel<Asignacion> asignacionesModel;
    private final JList<Asignacion> listaAsignaciones;
    private final JTextArea salidaArea;

    public AsignacionesPanel(SistemaEmergencias sistema, AsignacionFileStore store, NavigationController navigation) {
        this.sistema = sistema;
        this.store = store;
        this.navigation = navigation;

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("Asignaciones"), BorderLayout.WEST);
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarAsignaciones());
        header.add(btnActualizar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        asignacionesModel = new DefaultListModel<>();
        listaAsignaciones = new JList<>(asignacionesModel);
        listaAsignaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAsignaciones.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(formatoAsignacion(value));
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });
        listaAsignaciones.addListSelectionListener(e -> actualizarSalida());

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JScrollPane(listaAsignaciones), BorderLayout.CENTER);

        JButton btnAbrir = new JButton("Abrir bitacora");
        btnAbrir.addActionListener(e -> abrirBitacora());
        center.add(btnAbrir, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        salidaArea = new JTextArea(3, 40);
        salidaArea.setEditable(false);
        add(new JScrollPane(salidaArea), BorderLayout.SOUTH);

        cargarAsignaciones();
    }

    private void cargarAsignaciones() {
        asignacionesModel.clear();
        List<Asignacion> asignaciones = new ArrayList<>();
        try {
            Map<Integer, Brigadista> mapa = new HashMap<>();
            for (Brigadista item : sistema.getBrigadistas()) {
                mapa.put(item.getId(), item);
            }
            asignaciones.addAll(store.cargar(mapa));
        } catch (IOException e) {
            salidaArea.setText("No se pudieron cargar asignaciones: " + e.getMessage());
            return;
        }

        Usuario usuario = sistema.getUsuarioActual();
        if (usuario != null && Usuario.ROL_BRIGADISTA.equalsIgnoreCase(usuario.getRol())) {
            int idBrigadista = usuario.getId();
            for (Asignacion asignacion : asignaciones) {
                if (asignacion.getBrigadista().getId() == idBrigadista) {
                    asignacionesModel.addElement(asignacion);
                }
            }
        } else {
            for (Asignacion asignacion : asignaciones) {
                asignacionesModel.addElement(asignacion);
            }
        }

        if (!asignacionesModel.isEmpty()) {
            listaAsignaciones.setSelectedIndex(0);
        }
    }

    private void abrirBitacora() {
        Asignacion asignacion = listaAsignaciones.getSelectedValue();
        if (asignacion == null) {
            salidaArea.setText("Seleccione una asignacion.");
            return;
        }
        if (navigation != null) {
            JPanel panel = navigation.getPanel(KEY_BITACORA);
            if (panel instanceof BitacoraPanel) {
                ((BitacoraPanel) panel).setIncidenteId(asignacion.getIdIncidente());
            }
            navigation.show(KEY_BITACORA);
        }
    }

    private void actualizarSalida() {
        Asignacion asignacion = listaAsignaciones.getSelectedValue();
        if (asignacion == null) {
            salidaArea.setText("");
            return;
        }
        String texto = "Incidente " + asignacion.getIdIncidente() + " | Brigadista "
                + asignacion.getBrigadista().getNombre() + " | Estado " + asignacion.getEstado();
        salidaArea.setText(texto);
    }

    private String formatoAsignacion(Asignacion asignacion) {
        return "#" + asignacion.getIdIncidente() + " | "
                + asignacion.getBrigadista().getNombre() + " | " + asignacion.getEstado();
    }
}
