package GUI.panel;

import GUI.ValidationUI;
import Kernel.MovimientoInventario;
import Kernel.Suministro;
import Kernel.TipoMovimiento;
import Kernel.ValidacionException;
import Kernel.Validador;
import PersistenceLayer.InventarioFileStore;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class InventarioPanel extends JPanel {

    private final List<Suministro> suministros;
    private final Validador validador;
    private final InventarioFileStore store;

    private final DefaultTableModel tableModel;
    private final JTable tabla;
    private final JTextArea salidaArea;

    private final JTextField txtId;
    private final JTextField txtNombre;
    private final JTextField txtStock;
    private final JTextField txtStockMinimo;
    private final JTextField txtCantidadMovimiento;

    public InventarioPanel() {
        this.suministros = new ArrayList<>();
        this.validador = new Validador();
        this.store = new InventarioFileStore();

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Id", "Nombre", "Stock", "Minimo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(tableModel);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarDesdeTabla());

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtStock = new JTextField();
        txtStockMinimo = new JTextField();
        txtCantidadMovimiento = new JTextField();

        ValidationUI.configureField(txtId);
        ValidationUI.configureField(txtNombre);
        ValidationUI.configureField(txtStock);
        ValidationUI.configureField(txtStockMinimo);
        ValidationUI.configureField(txtCantidadMovimiento);

        ValidationUI.attachAutoClear(txtId);
        ValidationUI.attachAutoClear(txtNombre);
        ValidationUI.attachAutoClear(txtStock);
        ValidationUI.attachAutoClear(txtStockMinimo);
        ValidationUI.attachAutoClear(txtCantidadMovimiento);

        addField(form, gbc, 0, "Id:", txtId);
        addField(form, gbc, 1, "Nombre:", txtNombre);
        addField(form, gbc, 2, "Stock actual:", txtStock);
        addField(form, gbc, 3, "Stock minimo:", txtStockMinimo);
        addField(form, gbc, 4, "Cantidad movimiento:", txtCantidadMovimiento);

        JButton btnGuardar = new JButton("Guardar suministro");
        btnGuardar.addActionListener(e -> guardarSuministro());

        JButton btnEntrada = new JButton("Registrar entrada");
        btnEntrada.addActionListener(e -> aplicarMovimiento(TipoMovimiento.ENTRADA));

        JButton btnSalida = new JButton("Registrar salida");
        btnSalida.addActionListener(e -> aplicarMovimiento(TipoMovimiento.SALIDA));

        JButton btnRecargar = new JButton("Recargar inventario");
        btnRecargar.addActionListener(e -> cargarInventario());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        form.add(btnGuardar, gbc);
        gbc.gridy = 6;
        form.add(btnEntrada, gbc);
        gbc.gridy = 7;
        form.add(btnSalida, gbc);
        gbc.gridy = 8;
        form.add(btnRecargar, gbc);

        salidaArea = new JTextArea(3, 40);
        salidaArea.setEditable(false);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(new JScrollPane(salidaArea), BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        cargarInventario();
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private void guardarSuministro() {
        String id = validarTextoNoVacio(txtId, "id");
        if (id == null) {
            return;
        }
        String nombre = validarTextoNoVacio(txtNombre, "nombre");
        if (nombre == null) {
            return;
        }
        Integer stock = validarEntero(txtStock, "stock actual");
        if (stock == null || !validarNoNegativo(txtStock, stock, "stock actual")) {
            return;
        }
        Integer stockMinimo = validarEntero(txtStockMinimo, "stock minimo");
        if (stockMinimo == null || !validarNoNegativo(txtStockMinimo, stockMinimo, "stock minimo")) {
            return;
        }

        Suministro existente = buscarSuministro(id);
        if (existente == null) {
            suministros.add(new Suministro(id, nombre, stock, stockMinimo));
            salidaArea.setText("Suministro agregado: " + nombre);
        } else {
            actualizarSuministro(existente, nombre, stock, stockMinimo);
            salidaArea.setText("Suministro actualizado: " + nombre);
        }
        guardarInventario();
        refrescarTabla();
    }

    private void aplicarMovimiento(String tipoMovimiento) {
        String id = validarTextoNoVacio(txtId, "id");
        if (id == null) {
            return;
        }
        Integer cantidad = validarEnteroPositivo(txtCantidadMovimiento, "cantidad movimiento");
        if (cantidad == null) {
            return;
        }

        Suministro suministro = buscarSuministro(id);
        if (suministro == null) {
            salidaArea.setText("No se encontro suministro con id " + id);
            return;
        }

        MovimientoInventario movimiento = new MovimientoInventario(tipoMovimiento, cantidad, suministro);
        movimiento.aplicar();
        guardarInventario();
        refrescarTabla();
        salidaArea.setText("Movimiento " + tipoMovimiento + " aplicado para " + suministro.getNombre());
    }

    private void cargarDesdeTabla() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtNombre.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtStock.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtStockMinimo.setText(String.valueOf(tableModel.getValueAt(row, 3)));
    }

    private void refrescarTabla() {
        tableModel.setRowCount(0);
        for (Suministro suministro : suministros) {
            tableModel.addRow(new Object[]{
                    suministro.getId(),
                    suministro.getNombre(),
                    suministro.getStock(),
                    suministro.getStockMinimo()
            });
        }
    }

    private Suministro buscarSuministro(String id) {
        for (Suministro suministro : suministros) {
            if (suministro.getId().equalsIgnoreCase(id)) {
                return suministro;
            }
        }
        return null;
    }

    private void actualizarSuministro(Suministro suministro, String nombre, int stock, int stockMinimo) {
        suministro.setNombre(nombre);
        suministro.setStock(stock);
        suministro.setStockMinimo(stockMinimo);
    }

    private void cargarInventario() {
        try {
            suministros.clear();
            suministros.addAll(store.cargar());
            refrescarTabla();
            salidaArea.setText("Inventario cargado.");
        } catch (Exception ex) {
            salidaArea.setText("No se pudo cargar inventario: " + ex.getMessage());
        }
    }

    private void guardarInventario() {
        try {
            store.guardar(suministros);
        } catch (Exception ex) {
            salidaArea.setText("No se pudo guardar inventario: " + ex.getMessage());
        }
    }

    private boolean validarNoNegativo(JTextField field, int valor, String campo) {
        if (valor < 0) {
            ValidationUI.showFieldError(this, field, "El campo " + campo + " no puede ser negativo.");
            return false;
        }
        return true;
    }

    private String validarTextoNoVacio(JTextField field, String campo) {
        try {
            return validador.validarTextoNoVacio(field.getText(), campo);
        } catch (ValidacionException ex) {
            ValidationUI.showFieldError(this, field, ex.getMessage());
            return null;
        }
    }

    private Integer validarEntero(JTextField field, String campo) {
        try {
            return validador.convertirEntero(field.getText(), campo);
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
