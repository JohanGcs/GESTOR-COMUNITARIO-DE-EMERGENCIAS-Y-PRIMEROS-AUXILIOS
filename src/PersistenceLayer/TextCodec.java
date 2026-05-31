package PersistenceLayer;

import java.util.ArrayList;
import java.util.List;

public final class TextCodec {

    private TextCodec() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\\", "\\\\");
        escaped = escaped.replace("|", "\\|");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\r", "\\r");
        return escaped;
    }

    public static String join(String... parts) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                out.append('|');
            }
            out.append(escape(parts[i]));
        }
        return out.toString();
    }

    public static String[] split(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaping) {
                switch (ch) {
                    case 'n':
                        current.append('\n');
                        break;
                    case 'r':
                        current.append('\r');
                        break;
                    case '|':
                        current.append('|');
                        break;
                    case '\\':
                        current.append('\\');
                        break;
                    default:
                        current.append(ch);
                        break;
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '|') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        if (escaping) {
            current.append('\\');
        }
        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }
}

