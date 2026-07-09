/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
/**
 * Handles direct reading and writing of expense data in the CSV-style text file.
 * This is the data-layer component responsible for persisting and retrieving expenses.
 */
public class FileManager {
    private final Path filePath;

    public FileManager() {
        this(Paths.get("data", "expenses.txt"));
    }

    public FileManager(Path filePath) {
        this.filePath = filePath;
    }

    // Persists a new expense record to the data file so it can be loaded later.
    public void save(Expense e) throws IOException {
        ensureDataDirectory();
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(formatExpense(e));
            writer.newLine();
        }
    }

    // Loads all expense records from disk and returns them sorted by date in descending order.
    public List<Expense> loadAll() throws IOException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Expense> expenses = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            Expense expense = parseExpense(line);
            if (expense != null) {
                expenses.add(expense);
            }
        }
        sortExpensesDescending(expenses);
        return expenses;
    }

    // Removes the expense at the given index from the stored data set.
    public void delete(int indexToDelete) throws IOException {
        List<Expense> all = loadAll();
        if (indexToDelete < 0 || indexToDelete >= all.size()) {
            return;
        }
        all.remove(indexToDelete);
        ensureDataDirectory();
        List<String> lines = new ArrayList<>();
        for (Expense e : all) {
            lines.add(formatExpense(e));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Replaces an existing expense entry at the specified index with an updated version.
    public void updateExpense(int index, Expense updated) throws IOException {
        List<Expense> all = loadAll();
        if (index < 0 || index >= all.size()) {
            return;
        }
        all.set(index, updated);
        ensureDataDirectory();
        List<String> lines = new ArrayList<>();
        for (Expense e : all) {
            lines.add(formatExpense(e));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Renames every expense in a category and rewrites the file with the updated values.
    public void renameCategory(String oldCategory, String newCategory) throws IOException {
        List<Expense> all = loadAll();
        boolean changed = false;
        List<Expense> updated = new ArrayList<>();
        for (Expense e : all) {
            if (e.getCategory().equalsIgnoreCase(oldCategory)) {
                updated.add(new Expense(e.getDate(), newCategory.trim().toUpperCase(), e.getAmount(), e.getDescription()));
                changed = true;
            } else {
                updated.add(e);
            }
        }
        if (!changed) {
            return;
        }
        ensureDataDirectory();
        List<String> lines = new ArrayList<>();
        for (Expense e : updated) {
            lines.add(formatExpense(e));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Replaces all expenses in a category with an Uncategorized placeholder for data preservation.
    public void removeCategory(String category) throws IOException {
        if (category == null || category.isBlank()) {
            return;
        }
        List<Expense> all = loadAll();
        boolean changed = false;
        List<Expense> updated = new ArrayList<>();
        for (Expense e : all) {
            if (e.getCategory().equalsIgnoreCase(category)) {
                updated.add(new Expense(e.getDate(), "Uncategorized", e.getAmount(), e.getDescription()));
                changed = true;
            } else {
                updated.add(e);
            }
        }
        if (!changed) {
            return;
        }
        ensureDataDirectory();
        List<String> lines = new ArrayList<>();
        for (Expense e : updated) {
            lines.add(formatExpense(e));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String formatExpense(Expense e) {
        return e.getDate() + "," + e.getCategory() + "," + e.getAmount() + "," + e.getDescription();
    }

    private Expense parseExpense(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length < 4) {
            return null;
        }
        double amount;
        try {
            amount = Double.parseDouble(parts[2]);
        } catch (NumberFormatException ex) {
            return null;
        }
        return new Expense(parts[0], parts[1].trim().toUpperCase(), amount, parts[3]);
    }

    private void ensureDataDirectory() throws IOException {
        Path dir = filePath.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
    }

    private void sortExpensesDescending(List<Expense> expenses) {
        expenses.sort((a, b) -> b.getDate().compareTo(a.getDate()));
    }
}
