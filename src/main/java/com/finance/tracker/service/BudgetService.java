package com.finance.tracker.service;

import java.io.IOException;
import java.util.List;

import com.finance.tracker.dao.BudgetDAO;
import com.finance.tracker.dao.impl.BudgetFileDAO;
import com.finance.tracker.model.Budget;

public class BudgetService {
    private final BudgetDAO budgetDAO = new BudgetFileDAO();

    // Public method for creating a budget
    public void saveBudget(int categoryId, double amount, int month, int year) throws IOException {
        // If the budget amount is less than 0
        if (amount <= 0) {
            throw new IllegalArgumentException("Budget amount must be greater than zero.");
        }

        // If the month is invalid
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        // If validation checked are sccesfull, update the CSV inside the data
        budgetDAO.addOrUpdate(new Budget(0, categoryId, amount, month, year));
    }

    // Pulic method for deleting a budget
    public void deleteBudget(int id) throws IOException {
        budgetDAO.delete(id);
    }

    // Public method for listing all the budget objects
    public List<Budget> getAllBudgets() throws IOException {
        return budgetDAO.findAll();
    }
}
