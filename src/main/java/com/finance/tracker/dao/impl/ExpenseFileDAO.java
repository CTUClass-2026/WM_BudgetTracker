package com.finance.tracker.dao.impl;

import com.finance.tracker.dao.ExpenseDAO;
import com.finance.tracker.file.CsvUtil;
import com.finance.tracker.file.FileManager;
import com.finance.tracker.model.Expense;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFileDAO implements ExpenseDAO {
    @Override
    public void add(Expense expense) throws IOException {
        List<Expense> expenses = findAll();
        int nextId = expenses.stream().mapToInt(Expense::getId).max().orElse(0) + 1;
        expense.setId(nextId);
        expenses.add(expense);
        saveAll(expenses);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Expense> expenses = findAll();
        expenses.removeIf(e -> e.getId() == id);
        saveAll(expenses);
    }

    @Override
    public List<Expense> findAll() throws IOException {
        List<String> lines = Files.readAllLines(FileManager.EXPENSE_FILE);
        List<Expense> expenses = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                List<String> p = CsvUtil.parseLine(lines.get(i));
                expenses.add(new Expense(
                        Integer.parseInt(p.get(0)),
                        Double.parseDouble(p.get(1)),
                        p.get(2),
                        Integer.parseInt(p.get(3)),
                        LocalDate.parse(p.get(4)),
                        p.get(5),
                        LocalDateTime.parse(p.get(6))));
            }
        }
        return expenses;
    }

    @Override
    public List<Expense> findByMonth(int month, int year) throws IOException {
        return findAll().stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == month && e.getExpenseDate().getYear() == year)
                .toList();
    }

    private void saveAll(List<Expense> expenses) throws IOException {
        StringBuilder sb = new StringBuilder("id,amount,description,categoryId,expenseDate,paymentMethod,createdAt\n");
        for (Expense e : expenses) {
            sb.append(e.getId()).append(",")
              .append(e.getAmount()).append(",")
              .append(CsvUtil.escape(e.getDescription())).append(",")
              .append(e.getCategoryId()).append(",")
              .append(e.getExpenseDate()).append(",")
              .append(CsvUtil.escape(e.getPaymentMethod())).append(",")
              .append(e.getCreatedAt()).append("\n");
        }
        Files.writeString(FileManager.EXPENSE_FILE, sb.toString());
    }
}
