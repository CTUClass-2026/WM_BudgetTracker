package com.finance.tracker;

import com.finance.tracker.file.FileManager;
import com.finance.tracker.model.Category;
import com.finance.tracker.service.CategoryService;
import com.finance.tracker.ui.MainFrame;

/**
 * Application entry point for the personal finance tracker.
 */

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        FileManager.initializeFiles();
        seedDefaultCategories();

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    private static void seedDefaultCategories() {
        try {
            CategoryService categoryService = new CategoryService();

            if (categoryService.getAllCategories().isEmpty()) {
                categoryService.addCategory("Groceries", "#4CAF50");
                categoryService.addCategory("Transport", "#2196F3");
                categoryService.addCategory("Entertainment", "#FF9800");
                categoryService.addCategory("Utilities", "#9C27B0");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
