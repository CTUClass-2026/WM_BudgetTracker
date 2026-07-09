package com.mycompany.personalfinancetrackerctu;

import java.io.IOException;
import java.util.List;

/**
 * Service layer for expense operations.
 * Centralizes business logic and delegates persistence to FileManager.
 */
public class ExpenseService {
    private final FileManager fileManager;

    public static final ExpenseService DEFAULT = new ExpenseService(new FileManager());

    public ExpenseService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void addExpense(Expense e) throws IOException {
        fileManager.save(e);
    }

    public List<Expense> getAllExpenses() throws IOException {
        return fileManager.loadAll();
    }

    public void deleteExpense(int index) throws IOException {
        fileManager.delete(index);
    }

    public void updateExpense(int index, Expense updated) throws IOException {
        fileManager.updateExpense(index, updated);
    }

    public void renameCategory(String oldCategory, String newCategory) throws IOException {
        fileManager.renameCategory(oldCategory, newCategory);
    }

    public void deleteCategory(String category) throws IOException {
        fileManager.removeCategory(category);
    }
}
