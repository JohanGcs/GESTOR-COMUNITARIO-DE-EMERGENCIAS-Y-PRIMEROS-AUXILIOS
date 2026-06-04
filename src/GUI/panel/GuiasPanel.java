package GUI.panel;

import Kernel.NoEncontradoException;
import Kernel.SistemaEmergencias;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class GuiasPanel extends JPanel {

    private final SistemaEmergencias sistema;
    private final JTextArea output;
    private final JComboBox<String> cmbTipo;

    public GuiasPanel(SistemaEmergencias sistema) {
        this.sistema = sistema;

        setLayout(new BorderLayout());

        // Construir opciones del combo a partir de las guías registradas
        String[] tipos = sistema.getTiposConGuia();
        String[] opciones = new String[tipos.length + 1];
        opciones[0] = "-- Seleccione un tipo --";
        for (int i = 0; i < tipos.length; i++) {
            // Capitalizar primera letra para mejor presentación
            String t = tipos[i];
            opciones[i + 1] = t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase();
        }

        cmbTipo = new JComboBox<>(opciones);
        cmbTipo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbl = new JLabel("Tipo de emergencia:");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lbl, gbc);

        gbc.gridx = 1;
        form.add(cmbTipo, gbc);

        JButton btnBuscar = new JButton("Ver guia");
        btnBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnBuscar.addActionListener(e -> buscarGuia());

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        form.add(btnBuscar, gbc);

        output = new JTextArea(10, 40);
        output.setEditable(false);
        output.setFont(new Font("SansSerif", Font.PLAIN, 13));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void buscarGuia() {
        int index = cmbTipo.getSelectedIndex();
        if (index == 0) {
            output.setText("Por favor seleccione un tipo de emergencia.");
            return;
        }
        // El valor real se busca usando el tipo original (minúsculas) que coincide con las guias
        String[] tipos = sistema.getTiposConGuia();
        String tipoReal = tipos[index - 1];
        try {
            output.setText(sistema.buscarGuiaPorTipo(tipoReal));
        } catch (NoEncontradoException ex) {
            output.setText(ex.getMessage());
        }
    }
}
