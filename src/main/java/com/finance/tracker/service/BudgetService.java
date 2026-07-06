package com.finance.tracker.service;

import com.finance.tracker.dao.BudgetDAO;
import com.finance.tracker.dao.impl.BudgetFileDAO;
import com.finance.tracker.model.Budget;

import java.io.IOException;
import java.util.List;

public class BudgetService {
    private final BudgetDAO budgetDAO = new BudgetFileDAO();

    public void saveBudget(int categoryId, double amount, int month, int year) throws IOException {
        if (amount <= 0) throw new IllegalArgumentException("Budget amount must be greater than zero.");
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be between 1 and 12.");
        budgetDAO.addOrUpdate(new Budget(0, categoryId, amount, month, year));
    }

    public void deleteBudget(int id) throws IOException {
        budgetDAO.delete(id);
    }

    public List<Budget> getAllBudgets() throws IOException {
        return budgetDAO.findAll();
    }
}
