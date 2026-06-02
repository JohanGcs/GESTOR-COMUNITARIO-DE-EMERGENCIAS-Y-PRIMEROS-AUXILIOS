package GUI;

import java.awt.CardLayout;
import javax.swing.JPanel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationController {

    private final JPanel container;
    private final CardLayout layout;
    private final Map<String, JPanel> panelMap;
    private final Deque<String> history;
    private final List<NavigationListener> listeners;
    private String currentKey;

    public NavigationController(JPanel container) {
        this.container = container;
        this.layout = new CardLayout();
        this.container.setLayout(this.layout);
        this.panelMap = new HashMap<>();
        this.history = new ArrayDeque<>();
        this.listeners = new ArrayList<>();
    }

    public void addListener(NavigationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void addPanel(String key, JPanel panel) {
        if (panelMap.containsKey(key)) {
            return;
        }
        panelMap.put(key, panel);
        container.add(panel, key);
    }

    public void show(String key) {
        if (!panelMap.containsKey(key)) {
            return;
        }
        if (currentKey != null && !currentKey.equals(key)) {
            history.push(currentKey);
        }
        showInternal(key);
    }

    public void showIfExists(String key) {
        if (panelMap.containsKey(key)) {
            show(key);
        }
    }

    public void showDefault(String key, String fallbackKey) {
        if (panelMap.containsKey(key)) {
            showInternal(key);
        } else if (panelMap.containsKey(fallbackKey)) {
            showInternal(fallbackKey);
        }
    }

    public void goBack() {
        if (history.isEmpty()) {
            return;
        }
        showInternal(history.pop());
    }

    public boolean canGoBack() {
        return !history.isEmpty();
    }

    public void clearHistory() {
        history.clear();
        notifyListeners();
    }

    public boolean hasPanel(String key) {
        return panelMap.containsKey(key);
    }

    public JPanel getPanel(String key) {
        return panelMap.get(key);
    }

    private void showInternal(String key) {
        layout.show(container, key);
        currentKey = key;
        notifyListeners();
    }

    private void notifyListeners() {
        for (NavigationListener listener : listeners) {
            listener.onNavigate(currentKey, canGoBack());
        }
    }

    public interface NavigationListener {
        void onNavigate(String key, boolean canGoBack);
    }
}
