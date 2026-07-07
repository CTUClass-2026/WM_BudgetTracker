package com.finance.tracker.model;

/**
 * Represents a monthly spending budget for a category.
 */
public class Budget {
    private int id;
    private int categoryId;
    private double budgetAmount;
    private int budgetMonth;
    private int budgetYear;

    public Budget(int id, int categoryId, double budgetAmount, int budgetMonth, int budgetYear) {
        this.id = id;
        this.categoryId = categoryId;
        this.budgetAmount = budgetAmount;
        this.budgetMonth = budgetMonth;
        this.budgetYear = budgetYear;
    }

    // =====| Getters and setters
    // ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Budget 
    public double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(double budgetAmount) { this.budgetAmount = budgetAmount; }

    public int getBudgetMonth() { return budgetMonth; }
    public int getBudgetYear() { return budgetYear; }

    // Category

    public int getCategoryId() { return categoryId; }
    
    
}
