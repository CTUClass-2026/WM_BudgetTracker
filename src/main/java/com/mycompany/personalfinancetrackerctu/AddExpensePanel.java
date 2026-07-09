/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author ludwi
 */
public class AddExpensePanel extends JPanel {
    private static final String[] DEFAULT_CATEGORIES = {"GROCERIES", "UTILITIES", "TRANSPORT", "ENTERTAINMENT", "SAVINGS"};

    // Declare components as fields
    private JTextField tfDate;
    private JComboBox<String> categoryCombo;
    private JTextField tfAmount, tfDesc;
    private JComboBox<String> currencyCombo;
    private JButton btnSave;

    private final ExpenseService expenseService;
    private final LimitManager budgetManager;

    public AddExpensePanel() {
        this(ExpenseService.DEFAULT);
    }

    public AddExpensePanel(ExpenseService expenseService) {
        this.expenseService = expenseService;
        this.budgetManager = LimitManager.DEFAULT;
        setLayout(new java.awt.GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Theme.PLATINUM_BG);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new javax.swing.BoxLayout(formPanel, javax.swing.BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.setBackground(Theme.PLATINUM_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel headerLabel = new JLabel("Add a New Expense");
        headerLabel.setFont(headerLabel.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        headerLabel.setForeground(Theme.ACCENT);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(headerLabel);
        formPanel.add(Box.createVerticalStrut(12));

        tfDate = new JTextField();
        tfDate.setPreferredSize(new Dimension(260, 28));
        tfDate.setBackground(Theme.CARD_BG);
        tfDate.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(createFieldRow("Date (YYYY-MM-DD):", tfDate));
        formPanel.add(Box.createVerticalStrut(14));

        categoryCombo = new JComboBox<>(DEFAULT_CATEGORIES);
        categoryCombo.setEditable(true);
        categoryCombo.setPreferredSize(new Dimension(260, 28));
        categoryCombo.setBackground(Theme.CARD_BG);
        categoryCombo.setForeground(Theme.TEXT_PRIMARY);
        reloadCategoryOptions(false);
        configureCategoryAutocomplete();
        formPanel.add(createFieldRow("Category:", categoryCombo));
        formPanel.add(Box.createVerticalStrut(14));

        tfAmount = new JTextField();
        tfAmount.setPreferredSize(new Dimension(170, 28));
        tfAmount.setBackground(Theme.CARD_BG);
        tfAmount.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(createFieldRow("Amount:", tfAmount));
        formPanel.add(Box.createVerticalStrut(14));

        currencyCombo = new JComboBox<>(new String[]{"Rands", "Dollars"});
        currencyCombo.setPreferredSize(new Dimension(170, 28));
        currencyCombo.setBackground(Theme.CARD_BG);
        currencyCombo.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(createFieldRow("Currency:", currencyCombo));
        formPanel.add(Box.createVerticalStrut(14));

        tfDesc = new JTextField();
        tfDesc.setPreferredSize(new Dimension(260, 28));
        tfDesc.setBackground(Theme.CARD_BG);
        tfDesc.setForeground(Theme.TEXT_PRIMARY);
        formPanel.add(createFieldRow("Description:", tfDesc));
        formPanel.add(Box.createVerticalStrut(18));

        btnSave = new JButton("Save Expense");
        Theme.stylePrimaryButton(btnSave);
        JPanel buttonRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 0));
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonRow.setBackground(Theme.PLATINUM_BG);
        buttonRow.add(btnSave);
        formPanel.add(buttonRow);

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.NORTH;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        add(formPanel, gbc);

        // Action listener for save button
        btnSave.addActionListener(e -> {
            if (!LoginManager.requireLogin(this)) {
                JOptionPane.showMessageDialog(this, "Login required to add expenses.", "Login Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Login successful. You may now save the expense.", "Login Successful", JOptionPane.INFORMATION_MESSAGE);

            // 1. Read values from input controls
            String date = tfDate.getText().trim();
            String catInput = categoryCombo.getEditor().getItem().toString();
            String cat = CategoryValidator.normalizeCategory(catInput);
            String desc = tfDesc.getText().trim();
            String amountStr = tfAmount.getText().trim();
            String currency = currencyCombo.getSelectedItem().toString();

            // 2. Validate: fields must not be empty
            if (date.isEmpty() || catInput.trim().isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            if (cat == null) {
                JOptionPane.showMessageDialog(this, "Category must be a name and not a number.", "Invalid Category", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Validate date format strictly
            try {
                LocalDate parsedDate = LocalDate.parse(date);
                date = parsedDate.toString();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format!", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4. Parse the amount to a number
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(this, "Amount cannot be negative!");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for amount!");
                return;
            }

            try {
                if (isOverLimit(cat, LocalDate.parse(date), amount)) {
                    double limit = budgetManager.getLimit(cat);
                    double currentTotal = getCategoryMonthTotal(cat, LocalDate.parse(date));
                    int choice = JOptionPane.showConfirmDialog(this,
                            String.format("This expense would exceed the monthly limit for '%s'.\nCurrent month spent: %.2f\nLimit: %.2f\nAdd anyway?", cat, currentTotal, limit),
                            "Limit Exceeded", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                expenseService.addExpense(new Expense(date, cat, amount, desc));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to save expense: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tfDate.setText("");
            if (categoryCombo.getItemCount() > 0) {
                categoryCombo.setSelectedIndex(0);
            }
            tfAmount.setText("");
            currencyCombo.setSelectedIndex(0);
            tfDesc.setText("");
            JOptionPane.showMessageDialog(this, "Expense saved successfully! " + currency + " selected.");
            // Update view panel progress in real-time if present
            ViewExpensesPanel vp = findViewPanel();
            if (vp != null) {
                SwingUtilities.invokeLater(vp::refresh);
            }
        });
    }

    // Test helper: set fields and perform save programmatically
    public void setFields(String date, String category, String amount, String desc) {
        tfDate.setText(date);
        categoryCombo.setSelectedItem(category);
        tfAmount.setText(amount);
        tfDesc.setText(desc);
    }

    public void clickSave() {
        btnSave.doClick();
    }

    public void refreshCategoryOptions() {
        reloadCategoryOptions(true);
    }

    private void reloadCategoryOptions(boolean preserveEditorText) {
        String currentText = categoryCombo != null && categoryCombo.getEditor() != null
                ? String.valueOf(categoryCombo.getEditor().getItem())
                : "";
        Set<String> categories = new LinkedHashSet<>();
        Collections.addAll(categories, DEFAULT_CATEGORIES);
        categories.addAll(budgetManager.getAllLimits().keySet());
        try {
            for (Expense e : expenseService.getAllExpenses()) {
                if (e.getCategory() != null && !e.getCategory().isBlank()) {
                    categories.add(e.getCategory().trim().toUpperCase());
                }
            }
        } catch (IOException ignored) {
        }
        List<String> sorted = new ArrayList<>(categories);
        sorted.sort(String::compareToIgnoreCase);

        categoryCombo.removeAllItems();
        for (String category : sorted) {
            categoryCombo.addItem(category);
        }

        if (preserveEditorText && currentText != null && !currentText.isBlank()) {
            categoryCombo.getEditor().setItem(currentText);
        } else if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
    }

    private double getCategoryMonthTotal(String category, LocalDate date) throws IOException {
        double total = 0.0;
        YearMonth month = YearMonth.from(date);
        for (Expense e : expenseService.getAllExpenses()) {
            if (!e.getCategory().equalsIgnoreCase(category)) {
                continue;
            }
            try {
                LocalDate expenseDate = LocalDate.parse(e.getDate());
                if (YearMonth.from(expenseDate).equals(month)) {
                    total += e.getAmount();
                }
            } catch (Exception ignored) {
            }
        }
        return total;
    }

    private boolean isOverLimit(String category, LocalDate date, double amount) throws IOException {
        Double limit = budgetManager.getLimit(category);
        if (limit == null || limit <= 0) {
            return false;
        }
        double currentTotal = getCategoryMonthTotal(category, date);
        return currentTotal + amount > limit;
    }

    private void configureCategoryAutocomplete() {
        JTextField editor = (JTextField) categoryCombo.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }
                String typed = editor.getText();
                if (typed == null || typed.isBlank()) {
                    categoryCombo.hidePopup();
                    return;
                }
                String prefix = typed.trim().toUpperCase();
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    String option = String.valueOf(categoryCombo.getItemAt(i));
                    if (option.startsWith(prefix)) {
                        if (!option.equalsIgnoreCase(typed)) {
                            editor.setText(option);
                            editor.setSelectionStart(typed.length());
                            editor.setSelectionEnd(option.length());
                        }
                        categoryCombo.setPopupVisible(true);
                        return;
                    }
                }
                categoryCombo.hidePopup();
            }
        });
    }

    private JPanel createFieldRow(String labelText, java.awt.Component field) {
        JPanel row = new JPanel(new java.awt.BorderLayout(8, 8));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBackground(Theme.PLATINUM_BG);
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(170, 24));
        label.setForeground(Theme.TEXT_PRIMARY);
        row.add(label, java.awt.BorderLayout.WEST);
        row.add(field, java.awt.BorderLayout.CENTER);
        return row;
    }

    private ViewExpensesPanel findViewPanel() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return null;
        return findInContainer(w, ViewExpensesPanel.class);
    }

    private <T> T findInContainer(Container c, Class<T> cls) {
        for (Component ch : c.getComponents()) {
            if (cls.isInstance(ch)) {
                return cls.cast(ch);
            }
            if (ch instanceof Container container) {
                T found = findInContainer(container, cls);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}

