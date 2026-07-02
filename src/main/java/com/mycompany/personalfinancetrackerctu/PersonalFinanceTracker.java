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
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        }); // Added missing closing parenthesis here
    }
}

