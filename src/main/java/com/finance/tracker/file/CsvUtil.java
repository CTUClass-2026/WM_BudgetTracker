package com.finance.tracker.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for safely escaping and parsing CSV-formatted values.
 */
public class CsvUtil {
    private CsvUtil() {}

    // Escapes a field value so it can safely be written to CSV format.
    public static String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // Splits a single CSV line into fields while respecting quoted values.
    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                // Handles escaped double quotes inside a quoted field.
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                // A comma outside quotes marks a new column.
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        // Add the last field after the loop finishes.
        result.add(current.toString());
        return result;
    }
}
