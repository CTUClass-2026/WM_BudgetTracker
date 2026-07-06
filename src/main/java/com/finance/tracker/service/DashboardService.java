package com.finance.tracker.service;

import com.finance.tracker.model.Budget;
import com.finance.tracker.model.Category;
import com.finance.tracker.model.Expense;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {
    private final CategoryService categoryService = new CategoryService();
    private final ExpenseService expenseService = new ExpenseService();
    private final BudgetService budgetService = new BudgetService();

    public String generateDashboard(int month, int year) throws IOException {
        List<Category> categories = categoryService.getAllCategories();
        List<Expense> expenses = expenseService.getExpensesByMonth(month, year);
        List<Budget> budgets = budgetService.getAllBudgets();

        Map<Integer, Double> spendingByCategory = new HashMap<>();
        for (Expense e : expenses) {
            spendingByCategory.put(e.getCategoryId(), spendingByCategory.getOrDefault(e.getCategoryId(), 0.0) + e.getAmount());
        }

        double total = spendingByCategory.values().stream().mapToDouble(Double::doubleValue).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("PERSONAL FINANCE TRACKER DASHBOARD\n");
        sb.append("Month: ").append(month).append(" / ").append(year).append("\n");
        sb.append("=====================================\n\n");
        sb.append("Total Spending: R").append(String.format("%.2f", total)).append("\n\n");
        sb.append("SPENDING BY CATEGORY\n");
        sb.append("-------------------------------------\n");
        for (Category c : categories) {
            double spent = spendingByCategory.getOrDefault(c.getId(), 0.0);
            if (spent > 0) {
                sb.append(c.getName()).append(": R").append(String.format("%.2f", spent)).append("\n");
            }
        }

        sb.append("\nBUDGET STATUS\n");
        sb.append("-------------------------------------\n");
        for (Budget b : budgets) {
            if (b.getBudgetMonth() == month && b.getBudgetYear() == year) {
                String categoryName = categoryService.findById(b.getCategoryId()).map(Category::getName).orElse("Unknown");
                double spent = spendingByCategory.getOrDefault(b.getCategoryId(), 0.0);
                double percentage = (spent / b.getBudgetAmount()) * 100;
                String status = percentage >= 100 ? "EXCEEDED" : percentage >= 80 ? "WARNING" : "NORMAL";
                sb.append(categoryName).append(" | Budget: R").append(String.format("%.2f", b.getBudgetAmount()))
                  .append(" | Spent: R").append(String.format("%.2f", spent))
                  .append(" | ").append(String.format("%.1f", percentage)).append("% | ").append(status).append("\n");
            }
        }
        return sb.toString();
    }
}
