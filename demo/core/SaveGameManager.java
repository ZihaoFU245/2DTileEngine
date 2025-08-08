package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Manages writing/reading save files to disk as JSON without external deps.
 * The JSON format is simple and tailored to SaveData's fields.
 */
public class SaveGameManager {
    public static final String SAVE_DIR = "saves"; // relative to repo run dir

    public static void ensureSaveDir() throws IOException {
        Path dir = Path.of(SAVE_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    public static String makeDefaultSaveName() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        return fmt.format(new Date());
    }

    public static File fileForName(String baseName) {
        return Path.of(SAVE_DIR, baseName + ".json").toFile();
    }

    public static void write(SaveData d, String baseName) throws IOException {
        ensureSaveDir();
        File f = fileForName(baseName);
        try (BufferedWriter w = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"name\": \"").append(escape(d.name)).append("\",\n");
            sb.append("  \"savedAtEpochMs\": ").append(d.savedAtEpochMs).append(",\n");
            sb.append("  \"width\": ").append(d.width).append(",\n");
            sb.append("  \"height\": ").append(d.height).append(",\n");
            sb.append("  \"screenWidth\": ").append(d.screenWidth).append(",\n");
            sb.append("  \"screenHeight\": ").append(d.screenHeight).append(",\n");
            sb.append("  \"cellSize\": ").append(d.cellSize).append(",\n");
            sb.append("  \"seed\": ").append(d.seed).append(",\n");
            sb.append("  \"themeName\": \"").append(escape(d.themeName)).append("\",\n");
            sb.append("  \"playerX\": ").append(d.playerX).append(",\n");
            sb.append("  \"playerY\": ").append(d.playerY).append(",\n");
            sb.append("  \"ghosts\": [\n");
            for (int i = 0; i < d.ghosts.size(); i++) {
                SaveData.SavePos p = d.ghosts.get(i);
                sb.append("    { \"x\": ").append(p.x).append(", \"y\": ").append(p.y).append(" }");
                if (i < d.ghosts.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n");
            sb.append("}\n");
            w.write(sb.toString());
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static SaveData read(File f) throws IOException {
        String content;
        try (BufferedReader r = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
            content = sb.toString();
        }
        return parseJson(content);
    }

    // Very small JSON parser for the exact SaveData structure
    private static SaveData parseJson(String json) {
        SaveData d = new SaveData();
        d.name = extractString(json, "\"name\"");
        d.savedAtEpochMs = extractLong(json, "\"savedAtEpochMs\"");
        d.width = (int) extractLong(json, "\"width\"");
        d.height = (int) extractLong(json, "\"height\"");
        d.screenWidth = (int) extractLong(json, "\"screenWidth\"");
        d.screenHeight = (int) extractLong(json, "\"screenHeight\"");
        d.cellSize = (int) extractLong(json, "\"cellSize\"");
        d.seed = extractLong(json, "\"seed\"");
        d.themeName = extractString(json, "\"themeName\"");
        d.playerX = (int) extractLong(json, "\"playerX\"");
        d.playerY = (int) extractLong(json, "\"playerY\"");
        String ghostsArr = extractArray(json, "\"ghosts\"");
        if (ghostsArr != null) {
            // Split objects by '},{' boundaries; be forgiving
            String[] items = ghostsArr.split("\\},\\s*\\{");
            for (String it : items) {
                int x = (int) extractLong("{" + it + "}", "\"x\"");
                int y = (int) extractLong("{" + it + "}", "\"y\"");
                d.ghosts.add(new SaveData.SavePos(x, y));
            }
        }
        return d;
    }

    private static long extractLong(String json, String key) {
        int i = json.indexOf(key);
        if (i < 0) return 0L;
        int colon = json.indexOf(':', i);
        if (colon < 0) return 0L;
        int j = colon + 1;
        // Skip spaces
        while (j < json.length() && Character.isWhitespace(json.charAt(j))) j++;
        StringBuilder num = new StringBuilder();
        while (j < json.length()) {
            char c = json.charAt(j);
            if ((c >= '0' && c <= '9')) {
                num.append(c);
            } else if (c == '-') {
                num.append(c);
            } else {
                break;
            }
            j++;
        }
        try {
            return Long.parseLong(num.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    private static String extractString(String json, String key) {
        int i = json.indexOf(key);
        if (i < 0) return null;
        int colon = json.indexOf(':', i);
        if (colon < 0) return null;
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return null;
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return null;
        return unescape(json.substring(q1 + 1, q2));
    }

    private static String extractArray(String json, String key) {
        int i = json.indexOf(key);
        if (i < 0) return null;
        int colon = json.indexOf(':', i);
        if (colon < 0) return null;
        int lb = json.indexOf('[', colon + 1);
        if (lb < 0) return null;
        int rb = json.indexOf(']', lb + 1);
        if (rb < 0) return null;
        return json.substring(lb + 1, rb).trim();
    }

    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    public static List<File> listSaves() {
        List<File> result = new ArrayList<>();
        File dir = new File(SAVE_DIR);
        if (!dir.exists() || !dir.isDirectory()) return result;
        File[] files = dir.listFiles((d, name) -> name.toLowerCase(Locale.ROOT).endsWith(".json"));
        if (files != null) {
            for (File f : files) result.add(f);
        }
        result.sort((a, b) -> Long.compare(b.lastModified(), a.lastModified())); // newest first
        return result;
    }
}
