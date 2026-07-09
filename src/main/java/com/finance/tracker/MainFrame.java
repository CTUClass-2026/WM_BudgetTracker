/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class MainFrame extends JFrame {

    private final AddExpensePanel addExpensePanel;
    private final ViewExpensesPanel viewExpensesPanel;
    private final SpendAnalyticsPanel analyticsPanel;
    private final ExpenseService expenseService;

    public MainFrame() {
        this(ExpenseService.DEFAULT);
    }

    public MainFrame(ExpenseService expenseService) {
        this.expenseService = expenseService;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("CTU Finance Tracker™ 2026");
        this.setIconImage(IconFactory.createAppIcon());

        // Use BorderLayout so the tabbed pane automatically fills the window
        this.setLayout(new BorderLayout());
        this.setSize(1100, 760);
        this.setMinimumSize(new java.awt.Dimension(1000, 700));
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(Theme.APP_BG);

        UIManager.put("TabbedPane.selected", Theme.TAB_SELECTED_BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setName("mainTabs");
        tabs.setBackground(Theme.TAB_BG);
        tabs.setForeground(Theme.TEXT_PRIMARY);

        addExpensePanel = new AddExpensePanel(this.expenseService);
        tabs.addTab("Add Expense", addExpensePanel);
        viewExpensesPanel = new ViewExpensesPanel(this.expenseService);
        tabs.addTab("View Expenses", viewExpensesPanel);
        analyticsPanel = new SpendAnalyticsPanel(this.expenseService);
        tabs.addTab("Analytics", analyticsPanel);

        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setBackgroundAt(i, Theme.TAB_BG);
            tabs.setForegroundAt(i, Theme.TEXT_PRIMARY);
        }
        tabs.setBackgroundAt(tabs.getSelectedIndex(), Theme.TAB_SELECTED_BG);

        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                tabs.setBackgroundAt(i, Theme.TAB_BG);
            }
            tabs.setBackgroundAt(tabs.getSelectedIndex(), Theme.TAB_SELECTED_BG);

            if (tabs.getSelectedComponent() == addExpensePanel) {
                addExpensePanel.refreshCategoryOptions();
            } else if (tabs.getSelectedComponent() == viewExpensesPanel) {
                viewExpensesPanel.refresh();
            } else if (tabs.getSelectedComponent() == analyticsPanel) {
                analyticsPanel.refresh();
            }
        });

        this.add(tabs, BorderLayout.CENTER);
    }

}
