package com.mycompany.personalfinancetrackerctu;

/*
 * Imported project classes/files: none.
 */

/**
 * Validates and normalizes category names before they are stored or displayed.
 * This helper is used by the UI and service layers to prevent invalid category input.
 */
public class CategoryValidator {
    // Checks whether a category name is acceptable and not just a number or blank value.
    public static boolean isValidCategoryName(String category) {
        if (category == null) {
            return false;
        }
        String trimmed = category.trim();
        // Reject blank and numeric-only names like "123" or "123.45".
        return !trimmed.isEmpty() && !trimmed.matches("^\\d+(\\.\\d+)?$");
    }

    // Converts a user-entered category into a consistent uppercase value when it passes validation.
    public static String normalizeCategory(String category) {
        if (!isValidCategoryName(category)) {
            return null;
        }
        return category.trim().toUpperCase();
    }
}
