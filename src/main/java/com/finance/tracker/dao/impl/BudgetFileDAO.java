package com.finance.tracker.dao.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.finance.tracker.dao.BudgetDAO;
import com.finance.tracker.file.CsvUtil;
import com.finance.tracker.file.FileManager;
import com.finance.tracker.model.Budget;


public class BudgetFileDAO implements BudgetDAO {
    @Override
    public void addOrUpdate(Budget budget) throws IOException {
        List<Budget> budgets = findAll();
        Optional<Budget> existing = budgets.stream()
                .filter(b -> b.getCategoryId() == budget.getCategoryId() && b.getBudgetMonth() == budget.getBudgetMonth() && b.getBudgetYear() == budget.getBudgetYear())
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setBudgetAmount(budget.getBudgetAmount());
        } else {
            int nextId = budgets.stream().mapToInt(Budget::getId).max().orElse(0) + 1;
            budget.setId(nextId);
            budgets.add(budget);
        }
        saveAll(budgets);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Budget> budgets = findAll();
        budgets.removeIf(b -> b.getId() == id);
        saveAll(budgets);
    }

    @Override
    public List<Budget> findAll() throws IOException {
        List<String> lines = Files.readAllLines(FileManager.BUDGET_FILE);
        List<Budget> budgets = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                List<String> p = CsvUtil.parseLine(lines.get(i));
                budgets.add(new Budget(Integer.parseInt(p.get(0)), Integer.parseInt(p.get(1)), Double.parseDouble(p.get(2)), Integer.parseInt(p.get(3)), Integer.parseInt(p.get(4))));
            }
        }
        return budgets;
    }

    private void saveAll(List<Budget> budgets) throws IOException {
        StringBuilder sb = new StringBuilder("id,categoryId,budgetAmount,budgetMonth,budgetYear\n");
        for (Budget b : budgets) {
            sb.append(b.getId()).append(",")
              .append(b.getCategoryId()).append(",")
              .append(b.getBudgetAmount()).append(",")
              .append(b.getBudgetMonth()).append(",")
              .append(b.getBudgetYear()).append("\n");
        }
        Files.writeString(FileManager.BUDGET_FILE, sb.toString());
    }
}
