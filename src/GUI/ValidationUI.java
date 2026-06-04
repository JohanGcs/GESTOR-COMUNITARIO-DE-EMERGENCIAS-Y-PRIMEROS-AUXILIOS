package GUI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;
import java.util.WeakHashMap;

public final class ValidationUI {

    private static final Map<JComponent, Border> ORIGINAL_BORDERS = new WeakHashMap<>();

    /** Tamaño estándar para todos los campos de texto del sistema. */
    private static final Dimension FIELD_SIZE = new Dimension(260, 30);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private ValidationUI() {
    }

    /**
     * Aplica el tamaño y fuente estándar a un campo de texto.
     * Usar en todos los paneles para garantizar consistencia visual.
     */
    public static void configureField(JTextField field) {
        if (field == null) return;
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setFont(FIELD_FONT);
    }

    public static void showFieldError(Component parent, JComponent field, String message) {
        if (field != null) {
            markError(field);
            field.requestFocusInWindow();
        }
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void markError(JComponent field) {
        if (field == null) {
            return;
        }
        ORIGINAL_BORDERS.putIfAbsent(field, field.getBorder());
        field.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    public static void clearError(JComponent field) {
        if (field == null) {
            return;
        }
        Border original = ORIGINAL_BORDERS.get(field);
        if (original != null) {
            field.setBorder(original);
        }
    }

    public static void attachAutoClear(JTextComponent field) {
        if (field == null) {
            return;
        }
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearError(field);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearError(field);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearError(field);
            }
        });
    }

    public static void attachAutoClear(JPasswordField field) {
        if (field == null) {
            return;
        }
        attachAutoClear((JTextComponent) field);
    }

    public static void attachAutoClear(JComboBox<?> combo) {
        if (combo == null) {
            return;
        }
        combo.addActionListener(e -> clearError(combo));
    }

    public static void setDefaultButton(JRootPane rootPane, JButton button) {
        if (rootPane == null || button == null) {
            return;
        }
        rootPane.setDefaultButton(button);
    }
}
