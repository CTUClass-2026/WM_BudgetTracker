/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.personalfinancetrackerctu;

import javax.swing.SwingUtilities;

/**
 * 
 * @author ludwi
 */
public class PersonalFinanceTracker {
    
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

