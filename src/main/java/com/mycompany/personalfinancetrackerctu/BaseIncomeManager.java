package com.mycompany.personalfinancetrackerctu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores the base monthly income values used by the analytics budget calculations.
 * This helper keeps the budget data separate from the UI and service layers.
 */
public class BaseIncomeManager {
    private final Path filePath;
    private final Map<YearMonth, Double> baseIncomeByMonth = new LinkedHashMap<>();

    public static final BaseIncomeManager DEFAULT = new BaseIncomeManager();

    public BaseIncomeManager() {
        this(Paths.get("data", "base-income.txt"));
    }

    public BaseIncomeManager(Path filePath) {
        this.filePath = filePath;
        load();
    }

    // Saves a base income amount for a specific month so analytics can compare it with expenses.
    public synchronized void setBaseIncome(YearMonth month, double amount) {
        if (month == null || amount < 0) {
            return;
        }
        baseIncomeByMonth.put(month, amount);
        try {
            save();
        } catch (IOException ignored) {
        }
    }

    // Retrieves the stored base income value for the requested month, if one exists.
    public synchronized Double getBaseIncome(YearMonth month) {
        if (month == null) {
            return null;
        }
        return baseIncomeByMonth.get(month);
    }

    // Returns all stored base income records for use by the analytics and reporting features.
    public synchronized Map<YearMonth, Double> getAllBaseIncome() {
        return Collections.unmodifiableMap(baseIncomeByMonth);
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
                    YearMonth month = YearMonth.parse(parts[0].trim());
                    double amount = Double.parseDouble(parts[1].trim());
                    if (amount >= 0) {
                        baseIncomeByMonth.put(month, amount);
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
        for (Map.Entry<YearMonth, Double> entry : baseIncomeByMonth.entrySet()) {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(System.lineSeparator());
        }
        Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
