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
 * Persists and reads monthly income values for the analytics and budget calculations.
 * This class supports the service and presentation layers without coupling them to file handling.
 */
public class IncomeManager {
    private final Path filePath;
    private final Map<YearMonth, Double> incomeByMonth = new LinkedHashMap<>();

    public static final IncomeManager DEFAULT = new IncomeManager();

    public IncomeManager() {
        this(Paths.get("data", "income.txt"));
    }

    public IncomeManager(Path filePath) {
        this.filePath = filePath;
        load();
    }

    // Stores a monthly income value so analytics can calculate remaining budget.
    public synchronized void setIncome(YearMonth month, double amount) {
        if (month == null || amount < 0) {
            return;
        }
        incomeByMonth.put(month, amount);
        try {
            save();
        } catch (IOException ignored) {
        }
    }

    // Returns the saved income for a month when the analytics panel needs to display it.
    public synchronized Double getIncome(YearMonth month) {
        if (month == null) {
            return null;
        }
        return incomeByMonth.get(month);
    }

    public synchronized Map<YearMonth, Double> getAllIncome() {
        return Collections.unmodifiableMap(incomeByMonth);
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
                        incomeByMonth.put(month, amount);
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
        for (Map.Entry<YearMonth, Double> entry : incomeByMonth.entrySet()) {
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(System.lineSeparator());
        }
        Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}