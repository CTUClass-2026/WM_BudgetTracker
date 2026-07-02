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
 * @author ludwi
 */
public class FileManager {
    private static final Path FILE_PATH = Paths.get("data", "expenses.txt");

    public static void save(Expense e) throws IOException {
        ensureDataDirectory();
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(formatExpense(e));
            writer.newLine();
        }
    }

    public static List<Expense> loadAll() throws IOException {
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);
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
        return expenses;
    }

    public static void delete(int indexToDelete) throws IOException {
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
        Files.write(FILE_PATH, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String formatExpense(Expense e) {
        return e.getDate() + "," + e.getCategory() + "," + e.getAmount() + "," + e.getDescription();
    }

    private static Expense parseExpense(String line) {
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
        return new Expense(parts[0], parts[1], amount, parts[3]);
    }

    private static void ensureDataDirectory() throws IOException {
        Path dir = FILE_PATH.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
