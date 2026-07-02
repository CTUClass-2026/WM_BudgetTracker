/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author ludwi
 */
public class AddExpensePanel extends JPanel {

    // Declare components as fields
    private JTextField tfDate, tfCategory;
    private JTextField tfAmount, tfDesc;
    private JButton btnSave;

    public AddExpensePanel() {
        // GridLayout: 5 rows, 2 columns
        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Row 1 – Date
        add(new JLabel(" Date (YYYY-MM-DD):"));
        tfDate = new JTextField();
        add(tfDate);

        // Row 2 – Category
        add(new JLabel(" Category:"));
        tfCategory = new JTextField();
        add(tfCategory);

        // Row 3 – Amount
        add(new JLabel(" Amount:"));
        tfAmount = new JTextField();
        add(tfAmount);

        // Row 4 – Description
        add(new JLabel(" Description:"));
        tfDesc = new JTextField();
        add(tfDesc);

        // Row 5 – Save Button
        btnSave = new JButton("Save Expense");
        add(new JLabel()); // Empty spacer for GridLayout
        add(btnSave);

        // Action listener for save button
        btnSave.addActionListener(e -> {
            // 1. Read values from text fields
            String date = tfDate.getText().trim();
            String cat = tfCategory.getText().trim();
            String desc = tfDesc.getText().trim();
            String amountStr = tfAmount.getText().trim();

            // 2. Validate: fields must not be empty
            if (date.isEmpty() || cat.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            // 3. Parse the amount to a number
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
                FileManager.save(new Expense(date, cat, amount, desc));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to save expense: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tfDate.setText("");
            tfCategory.setText("");
            tfAmount.setText("");
            tfDesc.setText("");
            JOptionPane.showMessageDialog(this, "Expense saved successfully!");
        });
    }
}

