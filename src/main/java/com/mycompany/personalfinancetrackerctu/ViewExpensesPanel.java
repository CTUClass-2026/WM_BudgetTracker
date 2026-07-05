/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ViewExpensesPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnRefresh;
    private final JButton btnDelete;

    public ViewExpensesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRefresh = new JButton("Refresh");
        btnDelete = new JButton("Delete Selected");
        top.add(btnRefresh);
        top.add(btnDelete);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Date", "Category", "Amount", "Description"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> refresh());
        btnDelete.addActionListener(e -> deleteSelected());

        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        try {
            List<Expense> expenses = FileManager.loadAll();
            for (Expense e : expenses) {
                model.addRow(new Object[]{e.getDate(), e.getCategory(), e.getAmount(), e.getDescription()});
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to load expenses: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Delete selected expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            new ExpenseFileDAO().delete(row);
            refresh();
            JOptionPane.showMessageDialog(this, "Expense deleted.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to delete expense: " + ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
