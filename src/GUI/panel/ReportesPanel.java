package GUI.panel;

import Kernel.Incidente;
import Kernel.SistemaEmergencias;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

public class ReportesPanel extends JPanel {

    private final SistemaEmergencias sistema;
    private final JTextArea output;

    public ReportesPanel(SistemaEmergencias sistema) {
        this.sistema = sistema;
        this.output = new JTextArea(12, 40);
        this.output.setEditable(false);

        setLayout(new BorderLayout());

        JButton btnGenerar = new JButton("Generar reporte");
        btnGenerar.addActionListener(e -> generarReporte());

        add(btnGenerar, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void generarReporte() {
        Map<String, Integer> conteoPorTipo = new HashMap<>();
        int total = sistema.getCantidadIncidentes();
        Incidente[] incidentes = sistema.getIncidentes();

        for (int i = 0; i < total; i++) {
            Incidente incidente = incidentes[i];
            if (incidente == null) {
                continue;
            }
            String tipo = incidente.getTipo();
            conteoPorTipo.put(tipo, conteoPorTipo.getOrDefault(tipo, 0) + 1);
        }

        StringBuilder salida = new StringBuilder();
        salida.append("Total incidentes: ").append(total).append("\n");
        for (Map.Entry<String, Integer> entry : conteoPorTipo.entrySet()) {
            salida.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        if (total == 0) {
            salida.append("Sin incidentes registrados.");
        }

        output.setText(salida.toString());
    }
}


