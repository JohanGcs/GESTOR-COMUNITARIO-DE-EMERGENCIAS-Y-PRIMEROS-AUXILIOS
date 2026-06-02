package GUI.panel;

import GUI.ValidationUI;
import Kernel.Brigadista;
import Kernel.SistemaEmergencias;
import Kernel.Usuario;
import Kernel.ValidacionException;
import Kernel.Validador;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class BrigadistasPanel extends JPanel {

    private final SistemaEmergencias sistema;
    private final Validador validador;

    private final JTextArea output;
    private final JTextField txtId;
    private final JTextField txtNombre;
    private final JTextField txtCorreo;
    private final JTextField txtTelefono;

    public BrigadistasPanel(SistemaEmergencias sistema) {
        this.sistema = sistema;
        this.validador = new Validador();

        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtCorreo = new JTextField();
        txtTelefono = new JTextField();

        ValidationUI.attachAutoClear(txtId);
        ValidationUI.attachAutoClear(txtNombre);
        ValidationUI.attachAutoClear(txtCorreo);
        ValidationUI.attachAutoClear(txtTelefono);

        addField(form, gbc, 0, "Id:", txtId);
        addField(form, gbc, 1, "Nombre:", txtNombre);
        addField(form, gbc, 2, "Correo:", txtCorreo);
        addField(form, gbc, 3, "Telefono:", txtTelefono);

        JButton btnAgregar = new JButton("Agregar brigadista");
        btnAgregar.addActionListener(e -> agregarBrigadista());

        JButton btnDisponibilidad = new JButton("Cambiar disponibilidad");
        btnDisponibilidad.addActionListener(e -> cambiarDisponibilidad());

        JButton btnListar = new JButton("Listar brigadistas");
        btnListar.addActionListener(e -> listar());

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        form.add(btnAgregar, gbc);
        gbc.gridy = 5;
        form.add(btnDisponibilidad, gbc);
        gbc.gridy = 6;
        form.add(btnListar, gbc);

        output = new JTextArea(10, 40);
        output.setEditable(false);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private void agregarBrigadista() {
        Integer id = validarEnteroPositivo(txtId, "id");
        if (id == null) {
            return;
        }
        String nombre = validarTextoNoVacio(txtNombre, "nombre");
        if (nombre == null) {
            return;
        }
        String correo = validarTextoNoVacio(txtCorreo, "correo");
        if (correo == null) {
            return;
        }
        try {
            validador.validarEmailBasico(correo);
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, txtCorreo, ex.getMessage());
            return;
        }
        String telefono = validarTextoNoVacio(txtTelefono, "telefono");
        if (telefono == null) {
            return;
        }
        try {
            validador.validarTelefonoBasico(telefono);
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, txtTelefono, ex.getMessage());
            return;
        }

        Brigadista existente = sistema.buscarBrigadistaPorId(id);
        if (existente != null) {
            output.setText("Ya existe un brigadista con id " + id);
            return;
        }

        try {
            sistema.registrarUsuario(nombre, correo, telefono, "", Usuario.ROL_BRIGADISTA);
            output.setText("Brigadista agregado: " + nombre);
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, txtCorreo, ex.getMessage());
        }
    }

    private void cambiarDisponibilidad() {
        Integer id = validarEnteroPositivo(txtId, "id");
        if (id == null) {
            return;
        }
        Brigadista brigadista = sistema.buscarBrigadistaPorId(id);
        if (brigadista == null) {
            output.setText("No se encontro brigadista con id " + id);
            return;
        }
        try {
            sistema.cambiarDisponibilidadBrigadista(id, !brigadista.isDisponible());
            output.setText("Disponibilidad actualizada para " + brigadista.getNombre());
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, txtId, ex.getMessage());
        }
    }

    private void listar() {
        List<Brigadista> brigadistas = sistema.getBrigadistas();
        if (brigadistas.isEmpty()) {
            output.setText("Sin brigadistas registrados.");
            return;
        }
        StringBuilder salida = new StringBuilder();
        for (Brigadista brigadista : brigadistas) {
            salida.append(brigadista.getId())
                    .append(" | ")
                    .append(brigadista.getNombre())
                    .append(" | disponible: ")
                    .append(brigadista.isDisponible() ? "SI" : "NO")
                    .append("\n");
        }
        output.setText(salida.toString());
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
