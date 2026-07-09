package com.mycompany.personalfinancetrackerctu;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Presents charts and summaries for monthly spending, income, and recurring expenses.
 * This presentation-layer panel visualizes the data gathered through the service and data layers.
 */
public class SpendAnalyticsPanel extends JPanel {

    private final ExpenseService expenseService;
    private final IncomeManager incomeManager;
    private final BaseIncomeManager baseIncomeManager;
    private final RecurringExpenseManager recurringExpenseManager;
    private final MonthlyBudgetChartPanel budgetPanel;
    private final CategoryPieChartPanel piePanel;
    private final MonthlyBarChartPanel barPanel;
    private final DailyLineChartPanel linePanel;
    private final JLabel summaryLabel;
    private final JLabel budgetSummaryLabel;
    private final JLabel recurringListLabel;

    // Builds the analytics tab with charts, summary labels, and budget controls.
    public SpendAnalyticsPanel(ExpenseService expenseService) {
        this.expenseService = expenseService;
        this.incomeManager = IncomeManager.DEFAULT;
        this.baseIncomeManager = BaseIncomeManager.DEFAULT;
        this.recurringExpenseManager = RecurringExpenseManager.DEFAULT;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Theme.BEIGE_BG);

        JLabel header = new JLabel("Spending Analytics");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setForeground(Theme.ACCENT);
        add(header, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setBackground(Theme.BEIGE_BG);
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export CSV");
        Theme.styleSecondaryButton(refreshButton);
        Theme.styleSecondaryButton(exportButton);
        summaryLabel = new JLabel(" ");
        controls.add(refreshButton);
        controls.add(exportButton);
        controls.add(summaryLabel);

        JPanel budgetControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        budgetControls.setBackground(Theme.BEIGE_BG);
        JButton setIncomeButton = new JButton("Set Monthly Income");
        JButton addIncomeButton = new JButton("Add Additional Income");
        JButton removeIncomeButton = new JButton("Remove Additional Income");
        JButton setRecurringButton = new JButton("Set Recurring Expense");
        JButton removeRecurringButton = new JButton("Remove Recurring Expense");
        Theme.styleSecondaryButton(setIncomeButton);
        Theme.styleSecondaryButton(addIncomeButton);
        Theme.styleSecondaryButton(removeIncomeButton);
        Theme.styleSecondaryButton(setRecurringButton);
        Theme.styleSecondaryButton(removeRecurringButton);
        budgetSummaryLabel = new JLabel(" ");
        budgetControls.add(setIncomeButton);
        budgetControls.add(addIncomeButton);
        budgetControls.add(removeIncomeButton);
        budgetControls.add(setRecurringButton);
        budgetControls.add(removeRecurringButton);
        budgetControls.add(budgetSummaryLabel);

        JPanel charts = new JPanel();
        charts.setLayout(new BoxLayout(charts, BoxLayout.Y_AXIS));
        charts.setBackground(Theme.BEIGE_BG);

        piePanel = new CategoryPieChartPanel();
        piePanel.setPreferredSize(new Dimension(760, 260));
        piePanel.setBorder(BorderFactory.createTitledBorder("Category Breakdown (current month)"));
        piePanel.setBackground(Theme.CARD_BG);
        charts.add(piePanel);
        charts.add(Box.createVerticalStrut(10));

        barPanel = new MonthlyBarChartPanel();
        barPanel.setPreferredSize(new Dimension(760, 220));
        barPanel.setBorder(BorderFactory.createTitledBorder("Monthly Spending Trends"));
        barPanel.setBackground(Theme.CARD_BG);
        charts.add(barPanel);
        charts.add(Box.createVerticalStrut(10));

        linePanel = new DailyLineChartPanel();
        linePanel.setPreferredSize(new Dimension(760, 220));
        linePanel.setBorder(BorderFactory.createTitledBorder("Daily Spending Patterns (current month)"));
        linePanel.setBackground(Theme.CARD_BG);
        charts.add(linePanel);

        JPanel spendingTab = new JPanel(new BorderLayout(0, 8));
        spendingTab.setBackground(Theme.BEIGE_BG);
        spendingTab.add(controls, BorderLayout.NORTH);
        spendingTab.add(charts, BorderLayout.CENTER);

        budgetPanel = new MonthlyBudgetChartPanel();
        budgetPanel.setPreferredSize(new Dimension(760, 360));
        budgetPanel.setBorder(BorderFactory.createTitledBorder("Monthly Budget Calculator (Current Month)"));
        budgetPanel.setBackground(Theme.CARD_BG);

        recurringListLabel = new JLabel(" ");
        recurringListLabel.setVerticalAlignment(JLabel.TOP);
        JPanel recurringListPanel = new JPanel(new BorderLayout());
        recurringListPanel.setBackground(Theme.CARD_BG);
        recurringListPanel.setBorder(BorderFactory.createTitledBorder("Recurring Expenses"));
        JScrollPane recurringScroll = new JScrollPane(recurringListLabel);
        recurringScroll.setBorder(BorderFactory.createEmptyBorder());
        recurringScroll.getViewport().setBackground(Theme.CARD_BG);
        recurringListPanel.add(recurringScroll, BorderLayout.CENTER);
        recurringListPanel.setPreferredSize(new Dimension(280, 300));

        JPanel budgetCenter = new JPanel(new BorderLayout(8, 0));
        budgetCenter.setBackground(Theme.BEIGE_BG);
        budgetCenter.add(budgetPanel, BorderLayout.CENTER);
        budgetCenter.add(recurringListPanel, BorderLayout.EAST);

        JPanel budgetTab = new JPanel(new BorderLayout(0, 8));
        budgetTab.setBackground(Theme.BEIGE_BG);
        budgetTab.add(budgetControls, BorderLayout.NORTH);
        budgetTab.add(budgetCenter, BorderLayout.CENTER);

        JTabbedPane analyticsTabs = new JTabbedPane();
        analyticsTabs.addTab("Spending Analytics", spendingTab);
        analyticsTabs.addTab("Monthly Budget", budgetTab);

        add(analyticsTabs, BorderLayout.CENTER);

        setIncomeButton.addActionListener(e -> onSetMonthlyIncome());
        addIncomeButton.addActionListener(e -> onAddMonthlyIncome());
        removeIncomeButton.addActionListener(e -> onRemoveMonthlyIncome());
        setRecurringButton.addActionListener(e -> onSetRecurringExpense());
        removeRecurringButton.addActionListener(e -> onRemoveRecurringExpense());
        refreshButton.addActionListener(e -> refresh());
        exportButton.addActionListener(e -> exportCsv());
        SwingUtilities.invokeLater(this::refresh);
    }

    // Recomputes all charts and summaries from the latest expense and income data.
    public void refresh() {
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            YearMonth currentMonth = YearMonth.now();
            Map<String, Double> categoryTotals = getCategoryTotals(expenses, currentMonth);
            double monthTotal = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
            double recurringTotal = recurringExpenseManager.getTotalRecurringExpense();
            Map<String, Double> recurringItems = recurringExpenseManager.getAllRecurringExpenses();
            Double income = incomeManager.getIncome(currentMonth);
            Double baseIncome = baseIncomeManager.getBaseIncome(currentMonth);
            double additionalIncome = computeAdditionalIncome(income, baseIncome);
            budgetPanel.updateData(currentMonth, income, monthTotal, recurringTotal);
            refreshRecurringList(recurringItems, recurringTotal);
            piePanel.updateData(categoryTotals);
            barPanel.updateData(getMonthlyTotals(expenses));
            linePanel.updateData(getDailyTotals(expenses, currentMonth));
            summaryLabel.setText(generateMonthlySummary(currentMonth, categoryTotals, income));
            if (income == null) {
                budgetSummaryLabel.setText("Set monthly income to start tracking remaining budget.");
            } else {
                double monthlyRemaining = income - monthTotal - recurringTotal;
                budgetSummaryLabel.setText(String.format("%s income %.2f | additional %.2f | recurring %.2f | remaining %.2f",
                        currentMonth, income, additionalIncome, recurringTotal, monthlyRemaining));
            }
        } catch (IOException | RuntimeException ex) {
            budgetPanel.updateData(YearMonth.now(), null, 0.0, 0.0);
            refreshRecurringList(Collections.emptyMap(), 0.0);
            piePanel.updateData(Collections.emptyMap());
            barPanel.updateData(Collections.emptyMap());
            linePanel.updateData(Collections.emptyMap());
            summaryLabel.setText("Unable to compute summary.");
            budgetSummaryLabel.setText("Unable to compute monthly budget.");
        }
    }

    private void refreshRecurringList(Map<String, Double> recurringItems, double recurringTotal) {
        if (recurringItems == null || recurringItems.isEmpty()) {
            recurringListLabel.setText("<html><div style='padding:6px;'>No recurring expenses set.</div></html>");
            return;
        }

        Map<String, Double> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sorted.putAll(recurringItems);

        StringBuilder sb = new StringBuilder("<html><div style='padding:6px;'><table cellpadding='1' cellspacing='0'>");
        for (Map.Entry<String, Double> entry : sorted.entrySet()) {
            sb.append("<tr><td>")
                    .append(escapeHtml(entry.getKey()))
                    .append("</td><td width='90' align='right'>")
                    .append(String.format("%.2f", entry.getValue()))
                    .append("</td></tr>");
        }
        sb.append("</table><br><b>Total: ")
                .append(String.format("%.2f", recurringTotal))
                .append("</b></div></html>");
        recurringListLabel.setText(sb.toString());
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String generateMonthlySummary(YearMonth month, Map<String, Double> categoryTotals, Double income) {
        double total = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0) {
            if (income == null) {
                return "No expenses recorded for " + month + ".";
            }
            return String.format("%s income: %.2f; remaining: %.2f", month, income, income);
        }
        Map.Entry<String, Double> topCategory = categoryTotals.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);
        String topCategoryText = topCategory == null ? "None" : topCategory.getKey();
        if (income == null) {
            return String.format("%s total: %.2f; top category: %s", month, total, topCategoryText);
        }
        return String.format("%s total: %.2f; remaining income: %.2f; top category: %s", month, total, income - total, topCategoryText);
    }

    private double computeAdditionalIncome(Double income, Double baseIncome) {
        if (income == null) {
            return 0.0;
        }
        double base = baseIncome == null ? income : baseIncome;
        return Math.max(0.0, income - base);
    }

    // Opens a dialog that lets the user enter or update the monthly income value.
    private void onSetMonthlyIncome() {
        YearMonth now = YearMonth.now();
        Double existing = incomeManager.getIncome(now);
        String prompt = existing == null
                ? String.format("Set monthly income for %s:", now)
                : String.format("Set monthly income for %s (current: %.2f):", now, existing);
        String input = JOptionPane.showInputDialog(this, prompt, existing == null ? "" : String.valueOf(existing));
        if (input == null || input.isBlank()) {
            return;
        }
        try {
            double amount = Double.parseDouble(input.trim());
            if (amount < 0) {
                throw new NumberFormatException();
            }
            incomeManager.setIncome(now, amount);
            baseIncomeManager.setBaseIncome(now, amount);
            refresh();
            JOptionPane.showMessageDialog(this,
                    String.format("Monthly income set to %.2f for %s.", amount, now),
                    "Income Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive number for monthly income.",
                    "Invalid Income", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Adds extra income to the current month so the budget view can show additional earnings.
    private void onAddMonthlyIncome() {
        YearMonth now = YearMonth.now();
        Double existing = incomeManager.getIncome(now);
        double currentIncome = existing == null ? 0.0 : existing;
        Double existingBaseIncome = baseIncomeManager.getBaseIncome(now);
        String input = JOptionPane.showInputDialog(this,
            String.format("Add additional income for %s (current: %.2f):", now, currentIncome), "");
        if (input == null || input.isBlank()) {
            return;
        }
        try {
            double amount = Double.parseDouble(input.trim());
            if (amount < 0) {
                throw new NumberFormatException();
            }

            // For months saved before baseline tracking existed, pin baseline to
            // the pre-add value so the added delta is shown as additional income.
            if (existingBaseIncome == null) {
                baseIncomeManager.setBaseIncome(now, currentIncome);
            }

            double updated = currentIncome + amount;
            incomeManager.setIncome(now, updated);
            refresh();
            JOptionPane.showMessageDialog(this,
                    String.format("Additional income added. New monthly income: %.2f", updated),
                    "Income Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive number for additional income.",
                    "Invalid Income", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Removes previously added additional income from the current month when needed.
    private void onRemoveMonthlyIncome() {
        YearMonth now = YearMonth.now();
        Double existing = incomeManager.getIncome(now);
        if (existing == null || existing <= 0) {
            JOptionPane.showMessageDialog(this,
                    String.format("No monthly income available to remove for %s.", now),
                    "Nothing to Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Double storedBaseIncome = baseIncomeManager.getBaseIncome(now);
        double baseIncome = storedBaseIncome == null ? existing : storedBaseIncome;
        double additionalIncome = Math.max(0.0, existing - baseIncome);
        if (additionalIncome <= 0) {
            JOptionPane.showMessageDialog(this,
                String.format("No additional income available to remove for %s.", now),
                "Nothing to Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
            String.format("Remove additional income for %s (additional available: %.2f):", now, additionalIncome), "");
        if (input == null || input.isBlank()) {
            return;
        }

        try {
            double amount = Double.parseDouble(input.trim());
            if (amount < 0) {
                throw new NumberFormatException();
            }
                double amountToRemove = Math.min(amount, additionalIncome);
                double updated = Math.max(baseIncome, existing - amountToRemove);
            incomeManager.setIncome(now, updated);
            refresh();
            JOptionPane.showMessageDialog(this,
                    String.format("Additional income reduced by %.2f. New monthly income: %.2f", amountToRemove, updated),
                    "Income Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive number to remove.",
                    "Invalid Amount", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Opens a dialog for creating a recurring expense that is included in the monthly budget.
    private void onSetRecurringExpense() {
        JPanel form = new JPanel(new BorderLayout(8, 8));
        JLabel nameLabel = new JLabel("Expense name (e.g. RENT):");
        javax.swing.JTextField nameField = new javax.swing.JTextField(18);
        JLabel amountLabel = new JLabel("Monthly amount:");
        javax.swing.JTextField amountField = new javax.swing.JTextField(10);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.add(nameLabel);
        fields.add(Box.createVerticalStrut(4));
        fields.add(nameField);
        fields.add(Box.createVerticalStrut(8));
        fields.add(amountLabel);
        fields.add(Box.createVerticalStrut(4));
        fields.add(amountField);
        form.add(fields, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, form, "Set Recurring Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText();
        String amountInput = amountField.getText();
        if (name == null || name.isBlank() || amountInput == null || amountInput.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter both a name and amount.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountInput.trim());
            if (amount < 0) {
                throw new NumberFormatException();
            }
            recurringExpenseManager.setRecurringExpense(name, amount);
            refresh();
            JOptionPane.showMessageDialog(this,
                    String.format("Recurring expense '%s' set to %.2f.", name.trim().toUpperCase(), amount),
                    "Recurring Expense Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive number for recurring expense amount.",
                    "Invalid Amount", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Removes a stored recurring expense from the analytics budget calculations.
    private void onRemoveRecurringExpense() {
        Map<String, Double> recurring = recurringExpenseManager.getAllRecurringExpenses();
        if (recurring.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No recurring expenses are set.",
                    "Nothing to Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] names = recurring.keySet().toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select recurring expense to remove:",
                "Remove Recurring Expense",
                JOptionPane.PLAIN_MESSAGE,
                null,
                names,
                names[0]);

        if (selected == null || selected.isBlank()) {
            return;
        }
        recurringExpenseManager.removeRecurringExpense(selected);
        refresh();
    }

    private static class MonthlyBudgetChartPanel extends JPanel {
        private YearMonth month = YearMonth.now();
        private Double income;
        private double spent;
        private double recurringSpent;

        void updateData(YearMonth month, Double income, double spent, double recurringSpent) {
            this.month = month;
            this.income = income;
            this.spent = spent;
            this.recurringSpent = recurringSpent;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();

            if (income == null || income <= 0) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Set monthly income to view budget chart for " + month + ".", 20, 30);
                g2.dispose();
                return;
            }

            int diameter = Math.min(height - 130, 170);
            if (diameter < 80) {
                diameter = Math.min(height - 40, 120);
            }
            int x = Math.max(20, (width - diameter) / 2);

            double totalSpent = spent + recurringSpent;
            double remaining = Math.max(0, income - totalSpent);
            double over = Math.max(0, totalSpent - income);
            double totalChart = spent + recurringSpent + remaining + over;
            int legendRows = over > 0 ? 5 : 4;
            int legendHeight = legendRows * 22;
            int contentHeight = diameter + 24 + legendHeight;
            int y = Math.max(20, (height - contentHeight) / 2);

            int start = 0;
            int usedAngle = (int) Math.round((spent / totalChart) * 360.0);
            g2.setColor(new Color(82, 146, 255));
            g2.fillArc(x, y, diameter, diameter, start, usedAngle);
            start += usedAngle;

            if (recurringSpent > 0) {
                int recurringAngle = (int) Math.round((recurringSpent / totalChart) * 360.0);
                g2.setColor(new Color(125, 140, 141));
                g2.fillArc(x, y, diameter, diameter, start, recurringAngle);
                start += recurringAngle;
            }

            if (remaining > 0) {
                int remainingAngle = (int) Math.round((remaining / totalChart) * 360.0);
                g2.setColor(new Color(166, 196, 124));
                g2.fillArc(x, y, diameter, diameter, start, remainingAngle);
                start += remainingAngle;
            }

            if (over > 0) {
                int overAngle = 360 - start;
                g2.setColor(new Color(220, 90, 90));
                g2.fillArc(x, y, diameter, diameter, start, overAngle);
            }

            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x, y, diameter, diameter);

            int legendBlockWidth = 280;
            int legendX = Math.max(20, (width - legendBlockWidth) / 2);
            int legendY = y + diameter + 24;
            g2.setColor(Color.BLACK);
            g2.drawString("Month: " + month, legendX, legendY);
            legendY += 22;

            g2.setColor(new Color(82, 146, 255));
            g2.fillRect(legendX, legendY - 12, 14, 14);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("Logged expenses: %.2f", spent), legendX + 20, legendY);
            legendY += 22;

            g2.setColor(new Color(125, 140, 141));
            g2.fillRect(legendX, legendY - 12, 14, 14);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("Recurring expenses: %.2f", recurringSpent), legendX + 20, legendY);
            legendY += 22;

            g2.setColor(new Color(166, 196, 124));
            g2.fillRect(legendX, legendY - 12, 14, 14);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format("Remaining: %.2f", Math.max(0, income - totalSpent)), legendX + 20, legendY);
            legendY += 22;

            if (over > 0) {
                g2.setColor(new Color(220, 90, 90));
                g2.fillRect(legendX, legendY - 12, 14, 14);
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("Over budget: %.2f", over), legendX + 20, legendY);
            }

            g2.dispose();
        }
    }

    // Exports the current expense data to a CSV file chosen by the user.
    private void exportCsv() {
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            Path target = selectCsvFile();
            if (target == null) {
                return;
            }
            writeCsv(expenses, target);
            JOptionPane.showMessageDialog(this, "Expenses exported to " + target.toString(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Unable to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Path selectCsvFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Expenses to CSV");
        chooser.setSelectedFile(Paths.get("expenses_export.csv").toFile());
        int result = chooser.showSaveDialog(this);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().toPath() : null;
    }

    private void writeCsv(List<Expense> expenses, Path target) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Category,Amount,Description").append(System.lineSeparator());
        for (Expense e : expenses) {
            sb.append(escapeCsv(e.getDate())).append(',');
            sb.append(escapeCsv(e.getCategory())).append(',');
            sb.append(e.getAmount()).append(',');
            sb.append(escapeCsv(e.getDescription())).append(System.lineSeparator());
        }
        Path dir = target.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            writer.write(sb.toString());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private Map<String, Double> getCategoryTotals(List<Expense> expenses, YearMonth month) {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (Expense e : expenses) {
            try {
                LocalDate date = LocalDate.parse(e.getDate());
                if (YearMonth.from(date).equals(month)) {
                    String category = e.getCategory() == null ? "Uncategorized" : e.getCategory().trim();
                    totals.put(category, totals.getOrDefault(category, 0.0) + e.getAmount());
                }
            } catch (Exception ignored) {
            }
        }
        return totals;
    }


    private Map<YearMonth, Double> getMonthlyTotals(List<Expense> expenses) {
        YearMonth now = YearMonth.now();
        Map<YearMonth, Double> totals = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            totals.put(now.minusMonths(i), 0.0);
        }
        for (Expense e : expenses) {
            try {
                LocalDate date = LocalDate.parse(e.getDate());
                YearMonth ym = YearMonth.from(date);
                if (totals.containsKey(ym)) {
                    totals.put(ym, totals.get(ym) + e.getAmount());
                }
            } catch (Exception ignored) {
            }
        }
        return totals;
    }

    private Map<Integer, Double> getDailyTotals(List<Expense> expenses, YearMonth month) {
        int days = month.lengthOfMonth();
        Map<Integer, Double> totals = new LinkedHashMap<>();
        for (int day = 1; day <= days; day++) {
            totals.put(day, 0.0);
        }
        for (Expense e : expenses) {
            try {
                LocalDate date = LocalDate.parse(e.getDate());
                if (YearMonth.from(date).equals(month)) {
                    totals.put(date.getDayOfMonth(), totals.get(date.getDayOfMonth()) + e.getAmount());
                }
            } catch (Exception ignored) {
            }
        }
        return totals;
    }

    private static class CategoryPieChartPanel extends JPanel {
        private Map<String, Double> data = Collections.emptyMap();
        private final Color[] colors = {
                new Color(81, 169, 255),
                new Color(255, 148, 90),
                new Color(166, 196, 124),
                new Color(255, 204, 102),
                new Color(180, 157, 255),
                new Color(238, 118, 103),
                new Color(102, 204, 204)
        };

        void updateData(Map<String, Double> data) {
            this.data = data == null ? Collections.emptyMap() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            if (data.isEmpty()) {
                g2.drawString("No expense data for current month.", 20, 30);
                g2.dispose();
                return;
            }

            final int margin = 20;
            final int legendGap = 20;
            final int legendSwatch = 16;
            final int rowHeight = 22;
            boolean legendOnRight = width >= 540;

            int legendX;
            int legendY;
            int availableLegendWidth;

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            int diameter;
            if (legendOnRight) {
                int reservedLegendWidth = 220;
                diameter = Math.min(height - 2 * margin, width - reservedLegendWidth - (3 * margin));
                legendX = margin + diameter + legendGap;
                legendY = margin;
                availableLegendWidth = Math.max(120, width - legendX - margin);
            } else {
                diameter = Math.min(width - 2 * margin, height - 90);
                legendX = margin;
                legendY = margin + diameter + 12;
                availableLegendWidth = Math.max(120, width - 2 * margin);
            }

            if (diameter < 20) {
                g2.drawString("Chart area too small. Resize the window.", 20, 30);
                g2.dispose();
                return;
            }
            int x = margin;
            int y = legendOnRight ? Math.max(10, (height - diameter) / 2) : margin;
            int startAngle = 0;
            int idx = 0;
            int remainingSlices = data.size();
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double value = entry.getValue();
                double angle = value / total * 360.0;
                int arcAngle = (int) Math.round(angle);
                if (arcAngle <= 0 && value > 0) {
                    arcAngle = 1;
                }
                if (remainingSlices == 1) {
                    arcAngle = 360 - startAngle;
                }
                g2.setColor(colors[idx % colors.length]);
                g2.fillArc(x, y, diameter, diameter, startAngle, arcAngle);
                startAngle += arcAngle;
                idx++;
                remainingSlices--;
            }
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x, y, diameter, diameter);

            int maxRows = Math.max(1, (height - legendY - margin) / rowHeight);
            int columns = Math.max(1, (int) Math.ceil((double) data.size() / maxRows));
            int colWidth = Math.max(120, availableLegendWidth / columns);

            idx = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int col = idx / maxRows;
                int row = idx % maxRows;
                int drawX = legendX + col * colWidth;
                int drawY = legendY + row * rowHeight;
                g2.setColor(colors[idx % colors.length]);
                g2.fillRect(drawX, drawY, legendSwatch, legendSwatch);
                g2.setColor(Color.BLACK);
                String label = String.format("%s: %.2f", entry.getKey(), entry.getValue());
                String clipped = clipLabel(g2, label, colWidth - (legendSwatch + 8));
                g2.drawString(clipped, drawX + legendSwatch + 8, drawY + 13);
                idx++;
            }
            g2.dispose();
        }

        private String clipLabel(Graphics2D g2, String text, int maxWidth) {
            if (maxWidth <= 10) {
                return "...";
            }
            if (g2.getFontMetrics().stringWidth(text) <= maxWidth) {
                return text;
            }
            String ellipsis = "...";
            int end = text.length();
            while (end > 0) {
                String candidate = text.substring(0, end) + ellipsis;
                if (g2.getFontMetrics().stringWidth(candidate) <= maxWidth) {
                    return candidate;
                }
                end--;
            }
            return ellipsis;
        }
    }

    private static class MonthlyBarChartPanel extends JPanel {
        private Map<YearMonth, Double> data = Collections.emptyMap();

        void updateData(Map<YearMonth, Double> data) {
            this.data = data == null ? Collections.emptyMap() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            if (data.isEmpty()) {
                g2.drawString("No monthly spending data available.", 20, 30);
                g2.dispose();
                return;
            }
            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            int margin = 40;
            int barWidth = Math.max(24, (width - 2 * margin) / Math.max(1, data.size()) - 10);
            int x = margin;
            int idx = 0;
            for (Map.Entry<YearMonth, Double> entry : data.entrySet()) {
                int barHeight = (int) ((height - 2 * margin) * (entry.getValue() / Math.max(max, 1.0)));
                g2.setColor(new Color(82, 146, 255));
                g2.fillRect(x, height - margin - barHeight, barWidth, barHeight);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, height - margin - barHeight, barWidth, barHeight);
                g2.drawString(entry.getKey().toString(), x, height - margin + 15);
                g2.drawString(String.format("%.0f", entry.getValue()), x, height - margin - barHeight - 5);
                x += barWidth + 10;
                idx++;
            }
            g2.dispose();
        }
    }

    private static class DailyLineChartPanel extends JPanel {
        private Map<Integer, Double> data = Collections.emptyMap();

        void updateData(Map<Integer, Double> data) {
            this.data = data == null ? Collections.emptyMap() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            if (data.isEmpty()) {
                g2.drawString("No daily spending data available.", 20, 30);
                g2.dispose();
                return;
            }
            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            int margin = 40;
            int plotWidth = width - 2 * margin;
            int plotHeight = height - 2 * margin;
            int dayCount = data.size();
            int index = 0;
            int prevX = -1;
            int prevY = -1;
            for (Map.Entry<Integer, Double> entry : data.entrySet()) {
                int x = margin + (int) ((double) index / Math.max(1, dayCount - 1) * plotWidth);
                int y = height - margin - (int) ((entry.getValue() / Math.max(max, 1.0)) * plotHeight);
                g2.setColor(Color.BLUE);
                g2.fillOval(x - 3, y - 3, 6, 6);
                if (prevX != -1) {
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(prevX, prevY, x, y);
                }
                if (index % Math.max(1, dayCount / 10) == 0) {
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.valueOf(entry.getKey()), x - 6, height - margin + 15);
                }
                prevX = x;
                prevY = y;
                index++;
            }
            g2.setColor(Color.GRAY);
            g2.drawLine(margin, height - margin, margin + plotWidth, height - margin);
            g2.drawLine(margin, height - margin, margin, height - margin - plotHeight);
            g2.dispose();
        }
    }
}
