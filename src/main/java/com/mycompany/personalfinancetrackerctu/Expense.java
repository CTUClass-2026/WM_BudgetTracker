/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;

/**
 * Represents a single expense record used throughout the budget tracker.
 * The class stores the date, category, amount, and description in one
 * immutable object so the service and presentation layers can share it.
 */
public class Expense {
    private final String date;
    private final String category;
    private final double amount;
    private final String description;
    
    // Creates a new expense entry and normalizes the category and description for consistent storage.
    public Expense(String date, String category, double amount, String description){
        this.date = date;
        this.category = category == null ? null : category.trim().toUpperCase();
        this.amount = amount;
        this.description = description == null ? null : description.trim().toUpperCase();
    }
    // Returns the expense date when the record is displayed or processed by other layers.
    public String getDate() { return date;}
    // Returns the normalized category name associated with the expense.
    public String getCategory() { return category;}
    // Returns the monetary amount of the expense for calculations and summaries.
    public double getAmount() { return amount;}
    // Returns the description text entered for the expense entry.
    public String getDescription() { return description;}
}