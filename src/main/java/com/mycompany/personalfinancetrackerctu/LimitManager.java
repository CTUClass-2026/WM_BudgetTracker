package com.mycompany.personalfinancetrackerctu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LimitManager {
    private final Path filePath;
    private final Map<String, Double> limits = new LinkedHashMap<>();

    public static final LimitManager DEFAULT = new LimitManager();

    public LimitManager() {
        this(Paths.get("data", "limits.txt"));
    }

    public LimitManager(Path filePath) {
        this.filePath = filePath;
        load();
    }

    public synchronized void setLimit(String category, double amount) {
        String normalized = category == null ? null : category.trim().toUpperCase();
        if (normalized == null || normalized.isBlank()) {
            return;
        }
        limits.put(normalized, amount);
        try {
            save();
        } catch (IOException e) {
            // ignore persistence failures for now
        }
    }

    public synchronized Double getLimit(String category) {
        if (category == null) {
            return null;
        }
        return limits.get(category.trim().toUpperCase());
    }

    public synchronized Map<String, Double> getAllLimits() {
        return Collections.unmodifiableMap(limits);
    }

    public synchronized void renameCategory(String oldCategory, String newCategory) {
        if (oldCategory == null || newCategory == null || oldCategory.isBlank() || newCategory.isBlank()) {
            return;
        }
        String normalizedOld = oldCategory.trim().toUpperCase();
        String normalizedNew = newCategory.trim().toUpperCase();
        Double existing = limits.remove(normalizedOld);
        if (existing != null) {
            limits.put(normalizedNew, existing);
            try {
                save();
            } catch (IOException e) {
                // ignore persistence failures for now
            }
        }
    }

    public synchronized void removeLimit(String category) {
        if (category == null || category.isBlank()) {
            return;
        }
        String normalized = category.trim().toUpperCase();
        if (limits.remove(normalized) != null) {
            try {
                save();
            } catch (IOException e) {
                // ignore persistence failures for now
            }
        }
    }

    private void load() {
        try {
            if (Files.notExists(filePath)) return;
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line == null || line.isBlank()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length != 2) continue;
                try {
                    double v = Double.parseDouble(parts[1]);
                    String key = parts[0].trim().toUpperCase();
                    if (!key.isEmpty()) {
                        limits.put(key, v);
                    }
                } catch (NumberFormatException ex) {
                    // skip
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private void save() throws IOException {
        Path dir = filePath.getParent();
        if (dir != null && Files.notExists(dir)) Files.createDirectories(dir);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : limits.entrySet()) {
            sb.append(e.getKey()).append(",").append(e.getValue()).append(System.lineSeparator());
        }
        Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
