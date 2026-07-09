/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.personalfinancetrackerctu;

import javax.swing.SwingUtilities;

/**
 * Starts the application and launches the login, welcome, and main window flow.
 * This is the entry point for the presentation layer and should be invoked by the Java runtime.
 */
public class PersonalFinanceTracker {
    
    // Begins the application startup sequence and opens the main UI after authentication succeeds.
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            if (!LoginManager.authenticate(null)) {
                return;
            }
            WelcomeDialog welcomeDialog = new WelcomeDialog(null);
            if (!welcomeDialog.showDialog()) {
                return;
            }
            ExpenseService service = new ExpenseService(new FileManager());
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}

