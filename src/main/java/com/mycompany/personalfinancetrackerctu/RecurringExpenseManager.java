package com.mycompany.personalfinancetrackerctu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecurringExpenseManager {
    private final Path filePath;
    private final Map<String, Double> recurringExpenses = new LinkedHashMap<>();

    public static final RecurringExpenseManager DEFAULT = new RecurringExpenseManager();

    public RecurringExpenseManager() {
        this(Paths.get("data", "recurring-expenses.txt"));
    }

    public RecurringExpenseManager(Path filePath) {
        this.filePath = filePath;
        load();
    }

    public synchronized void setRecurringExpense(String name, double amount) {
        String normalized = name == null ? null : name.trim().toUpperCase();
        if (normalized == null || normalized.isBlank() || amount < 0) {
            return;
        }
        recurringExpenses.put(normalized, amount);
        try {
            save();
        } catch (IOException ignored) {
        }
    }

    public synchronized void removeRecurringExpense(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        String normalized = name.trim().toUpperCase();
        if (recurringExpenses.remove(normalized) != null) {
            try {
                save();
            } catch (IOException ignored) {
            }
        }
    }

    public synchronized double getTotalRecurringExpense() {
        return recurringExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public synchronized Map<String, Double> getAllRecurringExpenses() {
        return Collections.unmodifiableMap(recurringExpenses);
    }

    private void load() {
        try {
            if (Files.notExists(filePath)) {
                return;
            }
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", 2);
                if (parts.length != 2) {
                    continue;
                }
                try {
                    String key = parts[0].trim().toUpperCase();
                    double amount = Double.parseDouble(parts[1].trim());
                    if (!key.isBlank() && amount >= 0) {
                        recurringExpenses.put(key, amount);
                    }
                } catch (RuntimeException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void save() throws IOException {
        Path dir = filePath.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : recurringExpenses.entrySet()) {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(System.lineSeparator());
        }
        Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
