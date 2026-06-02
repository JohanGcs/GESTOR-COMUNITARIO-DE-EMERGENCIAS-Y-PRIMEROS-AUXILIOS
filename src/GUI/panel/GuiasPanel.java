package GUI.panel;

import GUI.ValidationUI;
import Kernel.NoEncontradoException;
import Kernel.SistemaEmergencias;
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

public class GuiasPanel extends JPanel {

    private final SistemaEmergencias sistema;
    private final Validador validador;
    private final JTextArea output;

    private final JTextField txtTipo;

    public GuiasPanel(SistemaEmergencias sistema) {
        this.sistema = sistema;
        this.validador = new Validador();

        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTipo = new JTextField();

        ValidationUI.attachAutoClear(txtTipo);

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Tipo de emergencia:"), gbc);
        gbc.gridx = 1;
        form.add(txtTipo, gbc);

        JButton btnBuscar = new JButton("Buscar guia");
        btnBuscar.addActionListener(e -> buscarGuia());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        form.add(btnBuscar, gbc);

        output = new JTextArea(10, 40);
        output.setEditable(false);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void buscarGuia() {
        String tipo = validarTextoNoVacio(txtTipo, "tipo guia");
        if (tipo == null) {
            return;
        }
        try {
            output.setText(sistema.buscarGuiaPorTipo(tipo));
        } catch (NoEncontradoException ex) {
            output.setText(ex.getMessage());
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
}
