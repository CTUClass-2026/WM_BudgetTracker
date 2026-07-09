package com.mycompany.personalfinancetrackerctu;

import java.io.IOException;
import java.util.List;

/**
 * Service layer for expense operations.
 * Centralizes business logic and delegates persistence to FileManager.
 */
/**
 * Coordinates expense operations between the presentation layer and the data layer.
 * This service class exposes business-level methods for creating, editing, and managing expenses.
 */
public class ExpenseService {
    private final FileManager fileManager;

    public static final ExpenseService DEFAULT = new ExpenseService(new FileManager());

    public ExpenseService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    // Adds a new expense through the service interface and delegates persistence to the data layer.
    public void addExpense(Expense e) throws IOException {
        fileManager.save(e);
    }

    // Retrieves all stored expenses so the UI can display or analyze them.
    public List<Expense> getAllExpenses() throws IOException {
        return fileManager.loadAll();
    }

    // Deletes the expense at a given position in the current list.
    public void deleteExpense(int index) throws IOException {
        fileManager.delete(index);
    }

    // Updates an existing expense entry with revised values.
    public void updateExpense(int index, Expense updated) throws IOException {
        fileManager.updateExpense(index, updated);
    }

    // Renames a category across all related expense records.
    public void renameCategory(String oldCategory, String newCategory) throws IOException {
        fileManager.renameCategory(oldCategory, newCategory);
    }

    // Removes a category by converting its expenses to the Uncategorized placeholder.
    public void deleteCategory(String category) throws IOException {
        fileManager.removeCategory(category);
    }
}
