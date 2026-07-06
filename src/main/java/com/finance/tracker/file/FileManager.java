package com.finance.tracker.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    public static final Path DATA_DIR = Path.of("data");
    public static final Path CATEGORY_FILE = DATA_DIR.resolve("categories.csv");
    public static final Path EXPENSE_FILE = DATA_DIR.resolve("expenses.csv");
    public static final Path BUDGET_FILE = DATA_DIR.resolve("budgets.csv");

    private FileManager() {}

    public static void initializeFiles() {
        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            createIfMissing(CATEGORY_FILE, "id,name,colorHex");
            createIfMissing(EXPENSE_FILE, "id,amount,description,categoryId,expenseDate,paymentMethod,createdAt");
            createIfMissing(BUDGET_FILE, "id,categoryId,budgetAmount,budgetMonth,budgetYear");
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize CSV files.", e);
        }
    }

    private static void createIfMissing(Path file, String header) throws IOException {
        if (!Files.exists(file)) {
            Files.writeString(file, header + System.lineSeparator());
        }
    }
}
