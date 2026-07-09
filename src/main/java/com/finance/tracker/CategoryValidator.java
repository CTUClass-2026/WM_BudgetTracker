package com.mycompany.personalfinancetrackerctu;

public class CategoryValidator {
    public static boolean isValidCategoryName(String category) {
        if (category == null) {
            return false;
        }
        String trimmed = category.trim();
        // Reject blank and numeric-only names like "123" or "123.45".
        return !trimmed.isEmpty() && !trimmed.matches("^\\d+(\\.\\d+)?$");
    }

    public static String normalizeCategory(String category) {
        if (!isValidCategoryName(category)) {
            return null;
        }
        return category.trim().toUpperCase();
    }
}
