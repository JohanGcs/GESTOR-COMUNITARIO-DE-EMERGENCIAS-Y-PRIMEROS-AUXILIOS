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
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final int LOGO_HEIGHT = 180;
    private static final int AVATAR_HEIGHT = 200;

    private JLabel avatarLabel;
    private JPanel logoUdWrapper;

    public LoginFrame() {
        this.sistema = new SistemaEmergencias();

        setTitle("Ingreso - Gestor Comunitario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        layout = new CardLayout();
        contenedor = new JPanel(layout);

        JPanel panelLogin = new JPanel(new BorderLayout());

        JPanel formLogin = new JPanel(new GridBagLayout());
        formLogin.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel tituloLogin = new JLabel("Iniciar sesion", SwingConstants.CENTER);
        tituloLogin.setFont(SUBTITLE_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formLogin.add(tituloLogin, gbc);

        txtLoginCorreo = new JTextField();
        txtLoginPassword = new JPasswordField();
        configureField(txtLoginCorreo);
        configureField(txtLoginPassword);
        ValidationUI.attachAutoClear(txtLoginCorreo);
        ValidationUI.attachAutoClear(txtLoginPassword);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setFont(LABEL_FONT);
        formLogin.add(lblCorreo, gbc);
        gbc.gridx = 1;
        formLogin.add(txtLoginCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblContrasena = new JLabel("Contrasena:");
        lblContrasena.setFont(LABEL_FONT);
        formLogin.add(lblContrasena, gbc);
        gbc.gridx = 1;
        formLogin.add(txtLoginPassword, gbc);

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnIrRegistro = new JButton("Crear cuenta");
        JButton btnSalir = new JButton("Salir");

        btnIngresar.setFont(BUTTON_FONT);
        btnIngresar.addActionListener(e -> iniciarSesion());

        btnIrRegistro.setFont(BUTTON_FONT);

        btnSalir.setFont(BUTTON_FONT);
        btnSalir.addActionListener(e -> salir());

        ValidationUI.setDefaultButton(getRootPane(), btnIngresar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formLogin.add(btnIngresar, gbc);
        gbc.gridy = 4;
        formLogin.add(btnIrRegistro, gbc);
        gbc.gridy = 5;
        formLogin.add(btnSalir, gbc);

        panelLogin.add(wrapCentered(formLogin), BorderLayout.CENTER);

        JPanel panelRegistro = new JPanel(new BorderLayout());
        JLabel tituloRegistro = new JLabel("Crear cuenta", SwingConstants.CENTER);
        tituloRegistro.setFont(TITLE_FONT);
        panelRegistro.add(tituloRegistro, BorderLayout.NORTH);

        JPanel formRegistro = new JPanel(new GridBagLayout());
        formRegistro.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbcReg = new GridBagConstraints();
        gbcReg.insets = new Insets(8, 8, 8, 8);
        gbcReg.fill = GridBagConstraints.HORIZONTAL;
        gbcReg.weightx = 1.0;

        txtRegNombre = new JTextField();
        txtRegCorreo = new JTextField();
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

        gbcReg.gridx = 0;
        gbcReg.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(LABEL_FONT);
        formRegistro.add(lblNombre, gbcReg);
        gbcReg.gridx = 1;
        formRegistro.add(txtRegNombre, gbcReg);

        gbcReg.gridx = 0;
        gbcReg.gridy = 1;
        JLabel lblRegCorreo = new JLabel("Correo:");
        lblRegCorreo.setFont(LABEL_FONT);
        formRegistro.add(lblRegCorreo, gbcReg);
        gbcReg.gridx = 1;
        formRegistro.add(txtRegCorreo, gbcReg);

        gbcReg.gridx = 0;
        gbcReg.gridy = 2;
        JLabel lblTelefono = new JLabel("Telefono:");
        lblTelefono.setFont(LABEL_FONT);
        formRegistro.add(lblTelefono, gbcReg);
        gbcReg.gridx = 1;
        formRegistro.add(txtRegTelefono, gbcReg);

        gbcReg.gridx = 0;
        gbcReg.gridy = 3;
        JLabel lblRegContrasena = new JLabel("Contrasena:");
        lblRegContrasena.setFont(LABEL_FONT);
        formRegistro.add(lblRegContrasena, gbcReg);
        gbcReg.gridx = 1;
        formRegistro.add(txtRegPassword, gbcReg);

        gbcReg.gridx = 0;
        gbcReg.gridy = 4;
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(LABEL_FONT);
        formRegistro.add(lblRol, gbcReg);
        gbcReg.gridx = 1;
        formRegistro.add(cmbRol, gbcReg);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnVolver = new JButton("Volver a login");

        btnRegistrar.setFont(BUTTON_FONT);
        btnRegistrar.addActionListener(e -> registrarCuenta());

        btnVolver.setFont(BUTTON_FONT);
        btnVolver.addActionListener(e -> {
            layout.show(contenedor, "login");
            setLogoUdVisible(true);
            ValidationUI.setDefaultButton(getRootPane(), btnIngresar);
        });

        btnIrRegistro.addActionListener(e -> {
            layout.show(contenedor, "registro");
            setLogoUdVisible(false);
            ValidationUI.setDefaultButton(getRootPane(), btnRegistrar);
        });

        gbcReg.gridx = 0;
        gbcReg.gridy = 5;
        gbcReg.gridwidth = 2;
        formRegistro.add(btnRegistrar, gbcReg);
        gbcReg.gridy = 6;
        formRegistro.add(btnVolver, gbcReg);

        JPanel registroCenter = new JPanel(new BorderLayout());
        registroCenter.add(formRegistro, BorderLayout.CENTER);
        registroCenter.add(buildAvatarWrapper(), BorderLayout.EAST);
        panelRegistro.add(wrapCentered(registroCenter), BorderLayout.CENTER);

        contenedor.add(panelLogin, "login");
        contenedor.add(panelRegistro, "registro");
        layout.show(contenedor, "login");

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("GESTOR COMUNITARIO DE EMERGENCIAS Y PRIMEROS AUXILIOS", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBorder(new EmptyBorder(12, 12, 12, 12));
        titleWrapper.add(title, BorderLayout.CENTER);
        root.add(titleWrapper, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.insets = new Insets(0, 0, 0, 0);
        centerGbc.gridy = 0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.weighty = 1.0;

        centerGbc.gridx = 0;
        centerGbc.weightx = 0.5;
        center.add(buildLogoWrapper("/Images/BRIGADE_LOGO.jpg"), centerGbc);

        centerGbc.gridx = 1;
        centerGbc.weightx = 0.0;
        center.add(contenedor, centerGbc);

        centerGbc.gridx = 2;
        centerGbc.weightx = 0.5;
        logoUdWrapper = buildLogoWrapper("/Images/LogoUD.png");
        center.add(logoUdWrapper, centerGbc);

        root.add(center, BorderLayout.CENTER);

        setContentPane(root);

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        updateAvatarForRole((String) cmbRol.getSelectedItem());
        setLogoUdVisible(true);
    }

    private JPanel buildLogoWrapper(String resourcePath) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBorder(new EmptyBorder(8, 24, 8, 24));
        JLabel label = buildLogoLabel(resourcePath);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(label, gbc);
        return wrapper;
    }

    private JLabel buildLogoLabel(String resourcePath) {
        JLabel label = new JLabel(loadScaledIcon(resourcePath, LOGO_HEIGHT));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel buildAvatarWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBorder(new EmptyBorder(8, 16, 8, 16));
        avatarLabel = new JLabel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(avatarLabel, gbc);
        return wrapper;
    }

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

    private void setLogoUdVisible(boolean visible) {
        if (logoUdWrapper != null) {
            logoUdWrapper.setVisible(visible);
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
            Usuario usuario = sistema.registrarUsuario(nombre, correo, telefono, password, (String) cmbRol.getSelectedItem());
            MainFrame mainFrame = new MainFrame(sistema, usuario.getRol());
            mainFrame.setVisible(true);
            dispose();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private JPanel wrapCentered(JPanel formPanel) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(formPanel, gbc);
        return wrapper;
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this, "Desea salir de la aplicacion?", "Confirmar salida",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }


    private void configureField(JTextField field) {
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setFont(LABEL_FONT);
    }
}
