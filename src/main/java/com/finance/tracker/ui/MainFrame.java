package com.finance.tracker.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.finance.tracker.model.Budget;
import com.finance.tracker.model.Category;
import com.finance.tracker.model.Expense;
import com.finance.tracker.service.BudgetService;
import com.finance.tracker.service.CategoryService;
import com.finance.tracker.service.DashboardService;
import com.finance.tracker.service.ExpenseService;

/**
 * Main Swing window for managing expenses, categories, budgets, and dashboard views.
 */
public class MainFrame extends JFrame {
    private final CategoryService categoryService = new CategoryService();
    private final ExpenseService expenseService = new ExpenseService();
    private final BudgetService budgetService = new BudgetService();
    private final DashboardService dashboardService = new DashboardService();

    private JComboBox<Category> expenseCategoryCombo;
    private JComboBox<Category> budgetCategoryCombo;
    private JTextField amountField;
    private JTextField descriptionField;
    private JTextField dateField;
    private JComboBox<String> paymentMethodCombo;
    private JTextField categoryNameField;
    private JTextField categoryColorField;
    private JTextField budgetAmountField;
    private JTextField budgetMonthField;
    private JTextField budgetYearField;
    private JTextArea dashboardArea;
    private JTextField dashboardMonthField;
    private JTextField dashboardYearField;
    private DefaultTableModel expenseTableModel;
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel budgetTableModel;

    public MainFrame() {
        setTitle("Personal Finance Tracker");
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Expense", createAddExpensePanel());
        tabs.addTab("Expenses", createExpensesPanel());
        tabs.addTab("Categories", createCategoriesPanel());
        tabs.addTab("Budgets", createBudgetsPanel());
        tabs.addTab("Dashboard", createDashboardPanel());

        add(tabs);
        refreshAll();
    }

    private JPanel createAddExpensePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        expenseCategoryCombo = new JComboBox<>();
        amountField = new JTextField();
        descriptionField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        paymentMethodCombo = new JComboBox<>(new String[]{"CASH", "CARD", "EFT", "DEBIT_CARD", "OTHER"});
        JButton saveButton = new JButton("Save Expense");
        saveButton.addActionListener(e -> saveExpense());

        panel.add(new JLabel("Category:")); panel.add(expenseCategoryCombo);
        panel.add(new JLabel("Amount:")); panel.add(amountField);
        panel.add(new JLabel("Description:")); panel.add(descriptionField);
        panel.add(new JLabel("Date YYYY-MM-DD:")); panel.add(dateField);
        panel.add(new JLabel("Payment Method:")); panel.add(paymentMethodCombo);
        panel.add(new JLabel("")); panel.add(saveButton);
        return panel;
    }

    private JPanel createExpensesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        expenseTableModel = new DefaultTableModel(new String[]{"ID", "Amount", "Description", "Category ID", "Date", "Payment"}, 0);
        JTable table = new JTable(expenseTableModel);
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showError("Please select an expense."); return; }
            try {
                int id = Integer.parseInt(expenseTableModel.getValueAt(row, 0).toString());
                expenseService.deleteExpense(id);
                refreshAll();
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(deleteButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCategoriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        categoryTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Colour"}, 0);
        JTable table = new JTable(categoryTableModel);
        categoryNameField = new JTextField(15);
        categoryColorField = new JTextField("#2196F3", 10);
        JButton addButton = new JButton("Add Category");
        addButton.addActionListener(e -> addCategory());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showError("Please select a category."); return; }
            try {
                int id = Integer.parseInt(categoryTableModel.getValueAt(row, 0).toString());
                categoryService.deleteCategory(id);
                refreshAll();
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        JPanel form = new JPanel();
        form.add(new JLabel("Name:")); form.add(categoryNameField);
        form.add(new JLabel("Colour:")); form.add(categoryColorField);
        form.add(addButton); form.add(deleteButton);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBudgetsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        budgetTableModel = new DefaultTableModel(new String[]{"ID", "Category ID", "Amount", "Month", "Year"}, 0);
        JTable table = new JTable(budgetTableModel);
        budgetCategoryCombo = new JComboBox<>();
        budgetAmountField = new JTextField(8);
        budgetMonthField = new JTextField(String.valueOf(LocalDate.now().getMonthValue()), 4);
        budgetYearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        JButton saveButton = new JButton("Save Budget");
        saveButton.addActionListener(e -> saveBudget());
        JPanel form = new JPanel();
        form.add(new JLabel("Category:")); form.add(budgetCategoryCombo);
        form.add(new JLabel("Amount:")); form.add(budgetAmountField);
        form.add(new JLabel("Month:")); form.add(budgetMonthField);
        form.add(new JLabel("Year:")); form.add(budgetYearField);
        form.add(saveButton);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        dashboardArea = new JTextArea();
        dashboardArea.setEditable(false);
        dashboardArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        dashboardMonthField = new JTextField(String.valueOf(LocalDate.now().getMonthValue()), 5);
        dashboardYearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> refreshDashboard());
        JPanel top = new JPanel();
        top.add(new JLabel("Month:")); top.add(dashboardMonthField);
        top.add(new JLabel("Year:")); top.add(dashboardYearField);
        top.add(refreshButton);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(dashboardArea), BorderLayout.CENTER);
        return panel;
    }

    private void saveExpense() {
        try {
            Category category = (Category) expenseCategoryCombo.getSelectedItem();
            if (category == null) { showError("Please select a category."); return; }
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            LocalDate date = LocalDate.parse(dateField.getText());
            String payment = paymentMethodCombo.getSelectedItem().toString();
            expenseService.addExpense(amount, description, category.getId(), date, payment);
            amountField.setText("");
            descriptionField.setText("");
            dateField.setText(LocalDate.now().toString());
            refreshAll();
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void addCategory() {
        try {
            categoryService.addCategory(categoryNameField.getText(), categoryColorField.getText());
            categoryNameField.setText("");
            categoryColorField.setText("#2196F3");
            refreshAll();
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void saveBudget() {
        try {
            Category category = (Category) budgetCategoryCombo.getSelectedItem();
            if (category == null) { showError("Please select a category."); return; }
            budgetService.saveBudget(category.getId(), Double.parseDouble(budgetAmountField.getText()), Integer.parseInt(budgetMonthField.getText()), Integer.parseInt(budgetYearField.getText()));
            budgetAmountField.setText("");
            refreshAll();
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void refreshAll() {
        refreshCategoryCombos();
        refreshCategories();
        refreshExpenses();
        refreshBudgets();
        refreshDashboard();
    }

    private void refreshCategoryCombos() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            expenseCategoryCombo.removeAllItems();
            budgetCategoryCombo.removeAllItems();
            for (Category c : categories) { expenseCategoryCombo.addItem(c); budgetCategoryCombo.addItem(c); }
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void refreshCategories() {
        try {
            categoryTableModel.setRowCount(0);
            for (Category c : categoryService.getAllCategories()) {
                categoryTableModel.addRow(new Object[]{c.getId(), c.getName(), c.getColorHex()});
            }
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void refreshExpenses() {
        try {
            expenseTableModel.setRowCount(0);
            for (Expense e : expenseService.getAllExpenses()) {
                expenseTableModel.addRow(new Object[]{e.getId(), e.getAmount(), e.getDescription(), e.getCategoryId(), e.getExpenseDate(), e.getPaymentMethod()});
            }
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void refreshBudgets() {
        try {
            budgetTableModel.setRowCount(0);
            for (Budget b : budgetService.getAllBudgets()) {
                budgetTableModel.addRow(new Object[]{b.getId(), b.getCategoryId(), b.getBudgetAmount(), b.getBudgetMonth(), b.getBudgetYear()});
            }
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void refreshDashboard() {
        try {
            int month = Integer.parseInt(dashboardMonthField.getText());
            int year = Integer.parseInt(dashboardYearField.getText());
            dashboardArea.setText(dashboardService.generateDashboard(month, year));
        } catch (Exception ex) { dashboardArea.setText("Could not load dashboard: " + ex.getMessage()); }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
