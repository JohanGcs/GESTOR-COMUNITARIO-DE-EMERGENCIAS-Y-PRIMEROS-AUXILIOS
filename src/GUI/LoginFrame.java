package GUI;

import Kernel.SistemaEmergencias;
import Kernel.Usuario;
import Kernel.ValidacionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class LoginFrame extends JFrame {

    private final SistemaEmergencias sistema;

    private final CardLayout layout;
    private final JPanel contenedor;

    private final JTextField txtLoginCorreo;
    private final JPasswordField txtLoginPassword;

    private final JTextField txtRegNombre;
    private final JTextField txtRegCorreo;
    private final JTextField txtRegTelefono;
    private final JPasswordField txtRegPassword;
    private final JComboBox<String> cmbRol;

    private static final Dimension FIELD_SIZE = new Dimension(320, 30);
    private static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 22);
    private static final Font LABEL_FONT  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final int LOGO_HEIGHT   = 180;
    private static final int AVATAR_HEIGHT = 200;

    private JLabel avatarLabel;

    public LoginFrame() {
        this.sistema = new SistemaEmergencias();

        setTitle("Ingreso - Gestor Comunitario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        layout     = new CardLayout();
        contenedor = new JPanel(layout);

        // ── Panel LOGIN ───────────────────────────────────────────────────────
        JPanel formLogin = new JPanel(new GridBagLayout());
        formLogin.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel tituloLogin = new JLabel("Iniciar sesion", SwingConstants.CENTER);
        tituloLogin.setFont(SUBTITLE_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formLogin.add(tituloLogin, gbc);

        txtLoginCorreo   = new JTextField();
        txtLoginPassword = new JPasswordField();
        configureField(txtLoginCorreo);
        configureField(txtLoginPassword);
        ValidationUI.attachAutoClear(txtLoginCorreo);
        ValidationUI.attachAutoClear(txtLoginPassword);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setFont(LABEL_FONT);
        formLogin.add(lblCorreo, gbc);
        gbc.gridx = 1;
        formLogin.add(txtLoginCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblContrasena = new JLabel("Contrasena:");
        lblContrasena.setFont(LABEL_FONT);
        formLogin.add(lblContrasena, gbc);
        gbc.gridx = 1;
        formLogin.add(txtLoginPassword, gbc);

        JButton btnIngresar  = new JButton("Ingresar");
        JButton btnIrRegistro = new JButton("Crear cuenta");
        JButton btnSalir     = new JButton("Salir");

        btnIngresar.setFont(BUTTON_FONT);
        btnIngresar.addActionListener(e -> iniciarSesion());
        btnIrRegistro.setFont(BUTTON_FONT);
        btnSalir.setFont(BUTTON_FONT);
        btnSalir.addActionListener(e -> salir());

        ValidationUI.setDefaultButton(getRootPane(), btnIngresar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formLogin.add(btnIngresar, gbc);
        gbc.gridy = 4; formLogin.add(btnIrRegistro, gbc);
        gbc.gridy = 5; formLogin.add(btnSalir, gbc);

        // Login panel: logo izquierdo | formulario centrado | logo derecho
        JPanel panelLogin = buildThreeColumnPanel(
                buildLogoWrapper("/Images/BRIGADE_LOGO.jpg"),
                wrapCentered(formLogin),
                buildLogoWrapper("/Images/LogoUD.png")
        );

        // ── Panel REGISTRO ────────────────────────────────────────────────────
        JPanel formRegistro = new JPanel(new GridBagLayout());
        formRegistro.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbcReg = new GridBagConstraints();
        gbcReg.insets  = new Insets(8, 8, 8, 8);
        gbcReg.fill    = GridBagConstraints.HORIZONTAL;
        gbcReg.weightx = 1.0;

        txtRegNombre   = new JTextField();
        txtRegCorreo   = new JTextField();
        txtRegTelefono = new JTextField();
        txtRegPassword = new JPasswordField();
        configureField(txtRegNombre);
        configureField(txtRegCorreo);
        configureField(txtRegTelefono);
        configureField(txtRegPassword);
        ValidationUI.attachAutoClear(txtRegNombre);
        ValidationUI.attachAutoClear(txtRegCorreo);
        ValidationUI.attachAutoClear(txtRegTelefono);
        ValidationUI.attachAutoClear(txtRegPassword);

        cmbRol = new JComboBox<>(new String[]{
                Usuario.ROL_RESIDENTE,
                Usuario.ROL_BRIGADISTA,
                Usuario.ROL_COORDINADOR
        });
        cmbRol.setFont(LABEL_FONT);
        ValidationUI.attachAutoClear(cmbRol);
        cmbRol.addActionListener(e -> updateAvatarForRole((String) cmbRol.getSelectedItem()));

        gbcReg.gridx = 0; gbcReg.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(LABEL_FONT);
        formRegistro.add(lblNombre, gbcReg);
        gbcReg.gridx = 1; formRegistro.add(txtRegNombre, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 1;
        JLabel lblRegCorreo = new JLabel("Correo:");
        lblRegCorreo.setFont(LABEL_FONT);
        formRegistro.add(lblRegCorreo, gbcReg);
        gbcReg.gridx = 1; formRegistro.add(txtRegCorreo, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 2;
        JLabel lblTelefono = new JLabel("Telefono:");
        lblTelefono.setFont(LABEL_FONT);
        formRegistro.add(lblTelefono, gbcReg);
        gbcReg.gridx = 1; formRegistro.add(txtRegTelefono, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 3;
        JLabel lblRegContrasena = new JLabel("Contrasena:");
        lblRegContrasena.setFont(LABEL_FONT);
        formRegistro.add(lblRegContrasena, gbcReg);
        gbcReg.gridx = 1; formRegistro.add(txtRegPassword, gbcReg);

        gbcReg.gridx = 0; gbcReg.gridy = 4;
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(LABEL_FONT);
        formRegistro.add(lblRol, gbcReg);
        gbcReg.gridx = 1; formRegistro.add(cmbRol, gbcReg);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnVolver    = new JButton("Volver a login");

        btnRegistrar.setFont(BUTTON_FONT);
        btnRegistrar.addActionListener(e -> registrarCuenta());

        btnVolver.setFont(BUTTON_FONT);
        btnVolver.addActionListener(e -> {
            layout.show(contenedor, "login");
            ValidationUI.setDefaultButton(getRootPane(), btnIngresar);
        });

        btnIrRegistro.addActionListener(e -> {
            layout.show(contenedor, "registro");
            ValidationUI.setDefaultButton(getRootPane(), btnRegistrar);
        });

        gbcReg.gridx = 0; gbcReg.gridy = 5; gbcReg.gridwidth = 2;
        formRegistro.add(btnRegistrar, gbcReg);
        gbcReg.gridy = 6; formRegistro.add(btnVolver, gbcReg);

        // El título "Crear cuenta" va como primera fila dentro del propio formRegistro
        // para que quede justo arriba de los campos
        JLabel tituloRegistro = new JLabel("Crear cuenta", SwingConstants.CENTER);
        tituloRegistro.setFont(TITLE_FONT);

        // Insertar el título al inicio: corremos todas las filas +1 y ponemos título en fila 0
        // Re-construimos el form con el título incluido
        JPanel formRegistroConTitulo = new JPanel(new GridBagLayout());
        formRegistroConTitulo.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gt = new GridBagConstraints();
        gt.insets  = new Insets(8, 8, 8, 8);
        gt.fill    = GridBagConstraints.HORIZONTAL;
        gt.weightx = 1.0;

        gt.gridx = 0; gt.gridy = 0; gt.gridwidth = 2;
        formRegistroConTitulo.add(tituloRegistro, gt);

        gt.gridwidth = 1;
        gt.gridx = 0; gt.gridy = 1;
        JLabel lbN2 = new JLabel("Nombre:"); lbN2.setFont(LABEL_FONT);
        formRegistroConTitulo.add(lbN2, gt);
        gt.gridx = 1; formRegistroConTitulo.add(txtRegNombre, gt);

        gt.gridx = 0; gt.gridy = 2;
        JLabel lbC2 = new JLabel("Correo:"); lbC2.setFont(LABEL_FONT);
        formRegistroConTitulo.add(lbC2, gt);
        gt.gridx = 1; formRegistroConTitulo.add(txtRegCorreo, gt);

        gt.gridx = 0; gt.gridy = 3;
        JLabel lbT2 = new JLabel("Telefono:"); lbT2.setFont(LABEL_FONT);
        formRegistroConTitulo.add(lbT2, gt);
        gt.gridx = 1; formRegistroConTitulo.add(txtRegTelefono, gt);

        gt.gridx = 0; gt.gridy = 4;
        JLabel lbP2 = new JLabel("Contrasena:"); lbP2.setFont(LABEL_FONT);
        formRegistroConTitulo.add(lbP2, gt);
        gt.gridx = 1; formRegistroConTitulo.add(txtRegPassword, gt);

        gt.gridx = 0; gt.gridy = 5;
        JLabel lbR2 = new JLabel("Rol:"); lbR2.setFont(LABEL_FONT);
        formRegistroConTitulo.add(lbR2, gt);
        gt.gridx = 1; formRegistroConTitulo.add(cmbRol, gt);

        gt.gridx = 0; gt.gridy = 6; gt.gridwidth = 2;
        formRegistroConTitulo.add(btnRegistrar, gt);
        gt.gridy = 7; formRegistroConTitulo.add(btnVolver, gt);

        // Registro panel: logo Brigade izquierdo | formulario centrado | avatar derecho
        JPanel panelRegistro = buildThreeColumnPanel(
                buildLogoWrapper("/Images/BRIGADE_LOGO.jpg"),
                wrapCentered(formRegistroConTitulo),
                buildAvatarWrapper()
        );

        contenedor.add(panelLogin,   "login");
        contenedor.add(panelRegistro, "registro");
        layout.show(contenedor, "login");

        // ── Root ──────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel(
                "GESTOR COMUNITARIO DE EMERGENCIAS Y PRIMEROS AUXILIOS",
                SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBorder(new EmptyBorder(12, 12, 12, 12));
        titleWrapper.add(title, BorderLayout.CENTER);
        root.add(titleWrapper, BorderLayout.NORTH);
        root.add(contenedor, BorderLayout.CENTER);

        JLabel creditos = new JLabel("Hecho por: Garavito Johan Camilo y Velandia Jose Luis");
        creditos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        creditos.setForeground(new java.awt.Color(150, 150, 150));
        creditos.setHorizontalAlignment(SwingConstants.RIGHT);
        creditos.setBorder(new EmptyBorder(6, 0, 0, 4));
        root.add(creditos, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        updateAvatarForRole((String) cmbRol.getSelectedItem());
    }

    // ── Helpers de layout ─────────────────────────────────────────────────────

    /**
     * Construye un panel de tres columnas simétricas:
     * columna izquierda y derecha con weightx=0.5, columna central con weightx=0.0.
     * Esto garantiza que la columna central siempre quede perfectamente centrada.
     */
    private JPanel buildThreeColumnPanel(JPanel left, JPanel center, JPanel right) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy   = 0;
        c.fill    = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        c.gridx   = 0;
        c.weightx = 0.5;
        panel.add(left, c);

        c.gridx   = 1;
        c.weightx = 0.0;
        panel.add(center, c);

        c.gridx   = 2;
        c.weightx = 0.5;
        panel.add(right, c);

        return panel;
    }

    private JPanel buildLogoWrapper(String resourcePath) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBorder(new EmptyBorder(8, 24, 8, 24));
        JLabel label = new JLabel(loadScaledIcon(resourcePath, LOGO_HEIGHT));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(label, gbc);
        return wrapper;
    }

    private JPanel buildAvatarWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBorder(new EmptyBorder(8, 16, 8, 16));
        avatarLabel = new JLabel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(avatarLabel, gbc);
        return wrapper;
    }


    private JPanel wrapCentered(JPanel formPanel) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(formPanel, gbc);
        return wrapper;
    }

    // ── Lógica ────────────────────────────────────────────────────────────────

    private void updateAvatarForRole(String rol) {
        String resourcePath = "/Images/Generic_Avatar.jpg";
        if (Usuario.ROL_RESIDENTE.equalsIgnoreCase(rol)) {
            resourcePath = "/Images/Residente_Avatar.jpg";
        } else if (Usuario.ROL_BRIGADISTA.equalsIgnoreCase(rol)) {
            resourcePath = "/Images/Brigadista_Avatar.jpg";
        } else if (Usuario.ROL_COORDINADOR.equalsIgnoreCase(rol)) {
            resourcePath = "/Images/Coordinador_Avatar.jpg";
        }
        if (avatarLabel != null) {
            avatarLabel.setIcon(loadScaledIcon(resourcePath, AVATAR_HEIGHT));
        }
    }

    private boolean validarEmailBasico(String correo) {
        return correo != null && correo.contains("@") && correo.contains(".");
    }

    private void iniciarSesion() {
        String correo = txtLoginCorreo.getText().trim();
        if (correo.isEmpty() || !validarEmailBasico(correo)) {
            ValidationUI.showFieldError(this, txtLoginCorreo, "El correo es obligatorio y debe ser valido.");
            return;
        }
        String password = new String(txtLoginPassword.getPassword()).trim();
        if (password.isEmpty()) {
            ValidationUI.showFieldError(this, txtLoginPassword, "La contrasena es obligatoria.");
            return;
        }
        try {
            Usuario usuario = sistema.login(correo, password);
            MainFrame mainFrame = new MainFrame(sistema, usuario.getRol());
            mainFrame.setVisible(true);
            dispose();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarCuenta() {
        String nombre = txtRegNombre.getText().trim();
        if (nombre.isEmpty()) {
            ValidationUI.showFieldError(this, txtRegNombre, "El nombre es obligatorio.");
            return;
        }
        String correo = txtRegCorreo.getText().trim();
        if (correo.isEmpty() || !validarEmailBasico(correo)) {
            ValidationUI.showFieldError(this, txtRegCorreo, "El correo es obligatorio y debe ser valido.");
            return;
        }
        String password = new String(txtRegPassword.getPassword()).trim();
        if (password.isEmpty()) {
            ValidationUI.showFieldError(this, txtRegPassword, "La contrasena es obligatoria.");
            return;
        }
        try {
            String telefono = txtRegTelefono.getText().trim();
            Usuario usuario = sistema.registrarUsuario(nombre, correo, telefono, password,
                    (String) cmbRol.getSelectedItem());
            MainFrame mainFrame = new MainFrame(sistema, usuario.getRol());
            mainFrame.setVisible(true);
            dispose();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this, "Desea salir de la aplicacion?",
                "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private ImageIcon loadScaledIcon(String resourcePath, int targetHeight) {
        java.net.URL resource = getClass().getResource(resourcePath);
        if (resource == null) return new ImageIcon();
        ImageIcon icon = new ImageIcon(resource);
        int width  = icon.getIconWidth();
        int height = icon.getIconHeight();
        if (width <= 0 || height <= 0) return icon;
        int targetWidth = (int) Math.round((double) width * targetHeight / height);
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void configureField(JTextField field) {
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setFont(LABEL_FONT);
    }
}
