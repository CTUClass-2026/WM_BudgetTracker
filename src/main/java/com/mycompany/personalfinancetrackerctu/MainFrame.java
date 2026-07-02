/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame {

    private final ViewExpensesPanel viewExpensesPanel;

    public MainFrame() {
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Personal Finance Tracker");
        
        // CHANGED: Use BorderLayout so the tabbed pane automatically fills the window
        this.setLayout(new BorderLayout()); 
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        
        tabs.addTab("Add Expense", new AddExpensePanel());
        viewExpensesPanel = new ViewExpensesPanel();
        tabs.addTab("View Expenses", viewExpensesPanel);

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabs.getSelectedComponent() == viewExpensesPanel) {
                    viewExpensesPanel.refresh();
                }
            }
        });
        
        this.add(tabs, BorderLayout.CENTER);
        this.setVisible(true);
    }

}
