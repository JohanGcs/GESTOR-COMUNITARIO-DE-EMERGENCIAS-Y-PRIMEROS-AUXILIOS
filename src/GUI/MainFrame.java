package GUI;

import GUI.panel.AsignacionesPanel;
import GUI.panel.BrigadistasPanel;
import GUI.panel.BitacoraPanel;
import GUI.panel.GuiasPanel;
import GUI.panel.IncidentesPanel;
import GUI.panel.InventarioPanel;
import GUI.panel.ReportesPanel;
import Kernel.Asignacion;
import Kernel.Brigadista;
import Kernel.SistemaEmergencias;
import Kernel.Usuario;
import PersistenceLayer.AsignacionFileStore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private static final String PANEL_INCIDENTES = "incidentes";
    private static final String PANEL_BITACORA = "bitacora";
    private static final String PANEL_GUIAS = "guias";
    private static final String PANEL_INVENTARIO = "inventario";
    private static final String PANEL_BRIGADISTAS = "brigadistas";
    private static final String PANEL_ASIGNACIONES = "asignaciones";
    private static final String PANEL_REPORTES = "reportes";

    private static final int AVATAR_HEIGHT = 96;

    private final NavigationController navigation;
    private final String rolUsuario;
    private final SistemaEmergencias sistema;

    public MainFrame(SistemaEmergencias sistema, String rolUsuario) {
        this.sistema = sistema;
        this.rolUsuario = (rolUsuario == null || rolUsuario.trim().isEmpty())
                ? Usuario.ROL_RESIDENTE
                : rolUsuario;
        setTitle("Gestor Comunitario - " + this.rolUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel content = new JPanel();
        navigation = new NavigationController(content);

        JPanel header = buildHeader();

        JPanel menu = new JPanel(new GridLayout(0, 1, 6, 6));
        menu.setBorder(new EmptyBorder(8, 8, 8, 8));
        menu.setPreferredSize(new Dimension(220, 0));

        JButton btnIncidentes = new JButton("Incidentes");
        JButton btnBitacora = new JButton("Bitacora");
        JButton btnGuias = new JButton("Guias");
        JButton btnInventario = new JButton("Inventario");
        JButton btnBrigadistas = new JButton("Brigadistas");
        JButton btnAsignaciones = new JButton("Asignaciones");
        JButton btnReportes = new JButton("Reportes");

        boolean esResidente = Usuario.ROL_RESIDENTE.equalsIgnoreCase(this.rolUsuario);
        boolean esBrigadista = Usuario.ROL_BRIGADISTA.equalsIgnoreCase(this.rolUsuario);
        boolean esCoordinador = Usuario.ROL_COORDINADOR.equalsIgnoreCase(this.rolUsuario);

        if (esResidente) {
            menu.add(btnIncidentes);
            menu.add(btnGuias);
        }
        if (esBrigadista) {
            menu.add(btnAsignaciones);
        }
        if (esCoordinador) {
            menu.add(btnIncidentes);
            menu.add(btnBitacora);
            menu.add(btnGuias);
            menu.add(btnInventario);
            menu.add(btnBrigadistas);
            menu.add(btnAsignaciones);
            menu.add(btnReportes);
        }


        AsignacionFileStore asignacionStore = new AsignacionFileStore();

        List<Brigadista> brigadistas = sistema.getBrigadistas();

        Map<Integer, Brigadista> brigadistasPorId = new HashMap<>();
        for (Brigadista brigadista : brigadistas) {
            brigadistasPorId.put(brigadista.getId(), brigadista);
        }

        List<Asignacion> asignaciones = new ArrayList<>();
        try {
            asignaciones.addAll(asignacionStore.cargar(brigadistasPorId));
        } catch (IOException e) {
            System.err.println("No se pudieron cargar asignaciones: " + e.getMessage());
        }

        navigation.addPanel(PANEL_INCIDENTES, new IncidentesPanel(sistema, this.rolUsuario, navigation));
        navigation.addPanel(PANEL_BITACORA, new BitacoraPanel(sistema, this.rolUsuario, navigation));
        navigation.addPanel(PANEL_GUIAS, new GuiasPanel(sistema));
        navigation.addPanel(PANEL_INVENTARIO, new InventarioPanel());
        navigation.addPanel(PANEL_BRIGADISTAS, new BrigadistasPanel(sistema));
        navigation.addPanel(PANEL_ASIGNACIONES, new AsignacionesPanel(sistema, asignacionStore, navigation));
        navigation.addPanel(PANEL_REPORTES, new ReportesPanel(sistema));

        btnIncidentes.addActionListener(e -> navigation.show(PANEL_INCIDENTES));
        btnBitacora.addActionListener(e -> navigation.show(PANEL_BITACORA));
        btnGuias.addActionListener(e -> navigation.show(PANEL_GUIAS));
        btnInventario.addActionListener(e -> navigation.show(PANEL_INVENTARIO));
        btnBrigadistas.addActionListener(e -> navigation.show(PANEL_BRIGADISTAS));
        btnAsignaciones.addActionListener(e -> navigation.show(PANEL_ASIGNACIONES));
        btnReportes.addActionListener(e -> navigation.show(PANEL_REPORTES));

        String panelInicial = PANEL_INCIDENTES;
        if (esBrigadista) {
            panelInicial = PANEL_ASIGNACIONES;
        }
        navigation.showDefault(panelInicial, PANEL_INCIDENTES);

        root.add(header, BorderLayout.NORTH);
        root.add(menu, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel userInfo = new JPanel(new BorderLayout(12, 0));
        JLabel avatar = new JLabel(loadScaledIcon(getAvatarPath(rolUsuario), AVATAR_HEIGHT));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setVerticalAlignment(SwingConstants.CENTER);

        JPanel userText = new JPanel(new GridLayout(0, 1));
        Usuario usuario = sistema.getUsuarioActual();
        String nombre = usuario == null ? "" : usuario.getNombre();
        String correo = usuario == null ? "" : usuario.getCorreo();
        String telefono = usuario == null ? "" : usuario.getTelefono();
        userText.add(new JLabel("Nombre: " + nombre));
        userText.add(new JLabel("Correo: " + correo));
        userText.add(new JLabel("Telefono: " + telefono));

        userInfo.add(avatar, BorderLayout.WEST);
        userInfo.add(userText, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 0, 8, 8));
        JButton btnVolver = new JButton("Volver");
        JButton btnCerrarSesion = new JButton("Cerrar sesion");
        JButton btnSalir = new JButton("Salir");

        btnVolver.addActionListener(e -> navigation.goBack());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        btnSalir.addActionListener(e -> System.exit(0));

        btnVolver.setEnabled(false);
        navigation.addListener((key, canGoBack) -> btnVolver.setEnabled(canGoBack));

        actions.add(btnVolver);
        actions.add(btnCerrarSesion);
        actions.add(btnSalir);

        header.add(userInfo, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private void cerrarSesion() {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        dispose();
    }

    private String getAvatarPath(String rol) {
        if (Usuario.ROL_BRIGADISTA.equalsIgnoreCase(rol)) {
            return "/Images/Brigadista_Avatar.jpg";
        }
        if (Usuario.ROL_COORDINADOR.equalsIgnoreCase(rol)) {
            return "/Images/Coordinador_Avatar.jpg";
        }
        return "/Images/Residente_Avatar.jpg";
    }

    private ImageIcon loadScaledIcon(String resourcePath, int targetHeight) {
        java.net.URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            return new ImageIcon();
        }
        ImageIcon icon = new ImageIcon(resource);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        if (width <= 0 || height <= 0) {
            return icon;
        }
        int targetWidth = (int) Math.round((double) width * targetHeight / height);
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
