package com.finance.tracker.service;

import com.finance.tracker.dao.ExpenseDAO;
import com.finance.tracker.dao.impl.ExpenseFileDAO;
import com.finance.tracker.model.Expense;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseService {
    private final ExpenseDAO expenseDAO = new ExpenseFileDAO();

    public void addExpense(double amount, String description, int categoryId, LocalDate date, String paymentMethod) throws IOException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        expenseDAO.add(new Expense(0, amount, description, categoryId, date, paymentMethod, LocalDateTime.now()));
    }

    public void deleteExpense(int id) throws IOException {
        expenseDAO.delete(id);
    }

    public List<Expense> getAllExpenses() throws IOException {
        return expenseDAO.findAll();
    }

    public List<Expense> getExpensesByMonth(int month, int year) throws IOException {
        return expenseDAO.findByMonth(month, year);
    }
}
