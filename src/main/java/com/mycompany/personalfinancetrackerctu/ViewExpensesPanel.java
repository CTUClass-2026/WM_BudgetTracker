/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;

/*
 * Imported project classes/files: ExpenseService, LimitManager, LoginManager, CategoryValidator, Expense, Theme.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Displays stored expenses in a table and shows monthly budget progress for each category.
 * This presentation-layer panel lets the user review, delete, and edit expenses.
 */
public class ViewExpensesPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnRefresh;
    private final JButton btnDelete;
    private final JButton btnEdit;
    private final JButton btnSetLimit;
    private final JButton btnRefreshLimits;
    private final JButton btnManageCategory;
    private final JPanel progressPanel;

    private final ExpenseService expenseService;
    private final LimitManager budgetManager;

    // Creates the expense overview table and its related action buttons.
    public ViewExpensesPanel() {
        this(ExpenseService.DEFAULT);
    }

    // Creates the expense overview panel with a provided service instance.
    public ViewExpensesPanel(ExpenseService expenseService) {
        this.expenseService = expenseService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Theme.PLATINUM_BG);

        JLabel header = new JLabel("Expenses (sorted by date descending)");
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setForeground(Theme.ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(Theme.PLATINUM_BG);
        btnRefresh = new JButton("Refresh");
        btnDelete = new JButton("Delete Selected");
        btnEdit = new JButton("Edit Selected");
        btnSetLimit = new JButton("Set/Edit Limit");
        btnRefreshLimits = new JButton("Refresh Monthly Limits");
        btnManageCategory = new JButton("Manage Categories");
        Theme.styleSecondaryButton(btnRefresh);
        Theme.styleSecondaryButton(btnDelete);
        Theme.styleSecondaryButton(btnEdit);
        Theme.styleSecondaryButton(btnSetLimit);
        Theme.styleSecondaryButton(btnRefreshLimits);
        Theme.styleSecondaryButton(btnManageCategory);
        top.add(btnRefresh);
        top.add(btnDelete);
        top.add(btnEdit);
        top.add(btnSetLimit);
        top.add(btnManageCategory);

        JPanel northPanel = new JPanel(new BorderLayout(0, 4));
        northPanel.setBackground(Theme.PLATINUM_BG);
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(top, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        String[] cols = {"Date", "Category", "Amount", "Description"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Theme.CARD_BG);
        table.setForeground(Theme.TEXT_PRIMARY);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.CARD_BG);
        add(scroll, BorderLayout.CENTER);

        // Progress panel on the right showing per-category monthly progress
        progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        progressPanel.setBackground(Theme.PLATINUM_BG);
        JScrollPane rightScroll = new JScrollPane(progressPanel);
        rightScroll.setBorder(BorderFactory.createTitledBorder("Monthly limits"));
        rightScroll.setPreferredSize(new Dimension(300, 0));
        rightScroll.getViewport().setBackground(Theme.PANEL_BG);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(Theme.PLATINUM_BG);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        refreshPanel.setBackground(Theme.PLATINUM_BG);
        refreshPanel.add(btnRefreshLimits);
        rightPanel.add(refreshPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        this.budgetManager = LimitManager.DEFAULT;

        btnRefresh.addActionListener(e -> refresh());
        btnDelete.addActionListener(e -> deleteSelected());
        btnEdit.addActionListener(e -> editSelected());
        btnSetLimit.addActionListener(e -> onSetLimit());
        btnRefreshLimits.addActionListener(e -> refreshMonthlyLimits());
        btnManageCategory.addActionListener(e -> onManageCategory());

        SwingUtilities.invokeLater(this::refresh);
    }

    // Reloads the expense table and budget progress information from the service layer.
    public void refresh() {
        model.setRowCount(0);
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            for (Expense e : expenses) {
                model.addRow(new Object[]{e.getDate(), e.getCategory(), e.getAmount(), e.getDescription()});
            }
            buildProgress(expenses);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to load expenses: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshMonthlyLimits() {
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            buildProgress(expenses);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to refresh monthly limits: " + ex.getMessage(), "Refresh Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildProgress(List<Expense> expenses) {
        progressPanel.removeAll();
        YearMonth now = YearMonth.now();
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : expenses) {
            try {
                LocalDate d = LocalDate.parse(e.getDate());
                YearMonth ym = YearMonth.from(d);
                if (!ym.equals(now)) continue;
            } catch (Exception ex) {
                continue;
            }
            totals.put(e.getCategory(), totals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        // include categories from limits even if zero spent
        Map<String, Double> limits = budgetManager.getAllLimits();
        List<String> cats = new ArrayList<>(limits.keySet());
        for (String c : totals.keySet()) if (!cats.contains(c)) cats.add(c);
        cats.sort(String::compareToIgnoreCase);

        if (cats.isEmpty()) {
            progressPanel.add(new JLabel("No categories or limits configured yet."));
        }

        for (String cat : cats) {
            double limit = limits.getOrDefault(cat, 0.0);
            double spent = totals.getOrDefault(cat, 0.0);
            double remaining = limit - spent;
            String labelText = limit > 0
                ? String.format("<html><b>%s</b><br/>Spent: %.2f | Limit: %.2f<br/>Remaining: %.2f</html>", cat, spent, limit, remaining)
                : String.format("<html><b>%s</b><br/>Spent: %.2f | Limit not set</html>", cat, spent);
            JLabel lbl = new JLabel(labelText);
            lbl.setToolTipText(limit > 0
                ? String.format("%s | Spent %.2f | Limit %.2f | Remaining %.2f", cat, spent, limit, remaining)
                : String.format("%s | Spent %.2f | Limit not set", cat, spent));
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setStringPainted(true);
            if (limit > 0) {
                int percent = (int) Math.min(100, Math.round((spent / limit) * 100));
                bar.setValue(percent);
                bar.setString(percent + "% used");
                if (spent > limit) {
                    bar.setForeground(java.awt.Color.RED);
                }
            } else {
                bar.setValue(0);
                bar.setString("No limit set");
            }
            progressPanel.add(lbl);
            progressPanel.add(bar);
            progressPanel.add(Box.createVerticalStrut(8));
        }
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    // Opens a dialog that lets the user set or edit a monthly category limit.
    private void onSetLimit() {
        String categoryInput = JOptionPane.showInputDialog(this, "Category to set/edit limit for:");
        if (categoryInput == null || categoryInput.isBlank()) return;
        String category = CategoryValidator.normalizeCategory(categoryInput);
        if (category == null) {
            JOptionPane.showMessageDialog(this, "Category must be a name and not a number.", "Invalid Category", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Double existingLimit = budgetManager.getLimit(category);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Monthly limit amount:"));
        JTextField limitField = new JTextField(existingLimit != null ? existingLimit.toString() : "");
        panel.add(limitField);
        panel.add(new JLabel("Currency:"));
        JComboBox<String> currencyOptions = new JComboBox<>(new String[]{"Rands", "Dollars"});
        panel.add(currencyOptions);

        int result = JOptionPane.showConfirmDialog(this, panel,
                existingLimit != null ? String.format("Monthly limit for '%s' (current: %.2f)", category, existingLimit) : String.format("Monthly limit for '%s'", category),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String lim = limitField.getText().trim();
        String currency = currencyOptions.getSelectedItem().toString();
        double v;
        try {
            v = Double.parseDouble(lim);
            if (v < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for limit.");
            return;
        }
        budgetManager.setLimit(category, v);
        JOptionPane.showMessageDialog(this,
                String.format("Limit set: %.2f %s for category '%s'.", v, currency, category),
                "Limit Saved", JOptionPane.INFORMATION_MESSAGE);
        try { refresh(); } catch (Exception ignore) {}
    }

    // Opens the category management dialog for renaming, deleting, or removing limits.
    private void onManageCategory() {
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            Set<String> categories = new LinkedHashSet<>(budgetManager.getAllLimits().keySet());
            for (Expense e : expenses) {
                if (e.getCategory() != null && !e.getCategory().isBlank()) {
                    categories.add(e.getCategory().trim().toUpperCase());
                }
            }
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No categories available to edit.", "Manage Categories", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<String> sorted = new ArrayList<>(categories);
            sorted.sort(String::compareToIgnoreCase);
            String selected = (String) JOptionPane.showInputDialog(this, "Select category to manage:", "Manage Categories",
                    JOptionPane.PLAIN_MESSAGE, null, sorted.toArray(String[]::new), sorted.get(0));
            if (selected == null || selected.isBlank()) {
                return;
            }

            Object[] actions = {"Rename category", "Delete category", "Delete limit only", "Cancel"};
            int action = JOptionPane.showOptionDialog(this,
                    "What would you like to do with category '" + selected + "'?",
                    "Manage Category",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    actions,
                    actions[0]);
            if (action == 1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete category '" + selected + "'? All expenses in this category will be marked Uncategorized and the limit will be removed.",
                        "Confirm Delete Category",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    expenseService.deleteCategory(selected);
                    budgetManager.removeLimit(selected);
                    refresh();
                }
                return;
            }
            if (action == 2) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete only the monthly limit for category '" + selected + "'? This will keep existing expenses intact.",
                        "Confirm Delete Limit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    budgetManager.removeLimit(selected);
                    refresh();
                }
                return;
            }
            if (action != 0) {
                return;
            }

            String newCategoryInput = JOptionPane.showInputDialog(this, "New name for category:", selected);
            if (newCategoryInput == null || newCategoryInput.isBlank()) {
                return;
            }
            String newCategory = CategoryValidator.normalizeCategory(newCategoryInput);
            if (newCategory == null) {
                JOptionPane.showMessageDialog(this, "Category must be a name and not a number.", "Invalid Category", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newCategory.equals(selected) && categories.contains(newCategory)) {
                JOptionPane.showMessageDialog(this, "A category with that name already exists.", "Duplicate Category", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newCategory.equals(selected)) {
                expenseService.renameCategory(selected, newCategory);
                budgetManager.renameCategory(selected, newCategory);
            }

            Double existingLimit = budgetManager.getLimit(newCategory);
            String prompt = existingLimit != null
                    ? String.format("Monthly limit amount for '%s' (current: %.2f):", newCategory, existingLimit)
                    : String.format("Monthly limit amount for '%s':", newCategory);
            String lim = JOptionPane.showInputDialog(this, prompt, existingLimit != null ? existingLimit.toString() : "");
            if (lim != null && !lim.isBlank()) {
                try {
                    double v = Double.parseDouble(lim);
                    if (v < 0) throw new NumberFormatException();
                    budgetManager.setLimit(newCategory, v);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid positive number for limit.");
                    return;
                }
            }
            refresh();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to manage categories: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Deletes the currently selected expense after confirmation and login checks.
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
            return;
        }

        if (!LoginManager.requireLogin(this)) {
            JOptionPane.showMessageDialog(this, "Login required to delete expenses.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Login successful. You may now delete the expense.", "Login Successful", JOptionPane.INFORMATION_MESSAGE);

        int choice;
        if (LoginManager.isTestMode()) {
            choice = JOptionPane.YES_OPTION; // auto-confirm in tests
        } else {
            choice = JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        }
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            expenseService.deleteExpense(row);
            refresh();
            JOptionPane.showMessageDialog(this, "Expense deleted.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to delete expense: " + ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Opens an edit dialog and saves the updated expense through the service layer.
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit.");
            return;
        }

        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            if (row < 0 || row >= expenses.size()) {
                JOptionPane.showMessageDialog(this, "Selected expense is no longer available.", "Edit Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Expense current = expenses.get(row);

            JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
            panel.add(new JLabel("Date (YYYY-MM-DD):"));
            JTextField dateField = new JTextField(current.getDate());
            panel.add(dateField);
            panel.add(new JLabel("Category:"));
            JComboBox<String> categoryField = new JComboBox<>(new String[]{"GROCERIES", "UTILITIES", "TRANSPORT", "ENTERTAINMENT", "SAVINGS"});
            categoryField.setEditable(true);
            categoryField.setSelectedItem(current.getCategory());
            panel.add(categoryField);
            panel.add(new JLabel("Amount:"));
            JTextField amountField = new JTextField(String.valueOf(current.getAmount()));
            panel.add(amountField);
            panel.add(new JLabel("Description:"));
            JTextField descField = new JTextField(current.getDescription());
            panel.add(descField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Edit Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String date = dateField.getText().trim();
            String categoryInput = categoryField.getEditor().getItem().toString();
            String category = CategoryValidator.normalizeCategory(categoryInput);
            String amountStr = amountField.getText().trim();
            String description = descField.getText().trim();

            if (date.isEmpty() || categoryInput.trim().isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in date, category, and amount.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (category == null) {
                JOptionPane.showMessageDialog(this, "Category must be a name and not a number.", "Invalid Category", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate.parse(date);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format!", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(this, "Amount cannot be negative!", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for amount!", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Expense updated = new Expense(date, category, amount, description);
            expenseService.updateExpense(row, updated);
            refresh();
            JOptionPane.showMessageDialog(this, "Expense updated successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to update expense: " + ex.getMessage(), "Edit Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Test helpers
    // Selects a row in the table for automated UI tests or programmatic interaction.
    public void selectRow(int row) {
        table.setRowSelectionInterval(row, row);
    }

    // Triggers the delete action on the selected row through the button handler.
    public void clickDelete() {
        btnDelete.doClick();
    }
}
