package com.finance.tracker.model;

/**
 * Represents a spending category that can be assigned to expenses.
 */
public class Category {
    private int id;
    private String name;
    private String colorHex;

    // Constructor
    public Category(int id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    // =====| Getters and setters
    // ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Name
    public String getName() { return name; }

    // Color
    public String getColorHex() { return colorHex; }

    @Override
    public String toString() {
        return name;
    }
}
