package com.finance.tracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single expense record with its amount, date, and payment details.
 */
public class Expense {
    private int id;
    private double amount;
    private String description;
    private int categoryId;
    private LocalDate expenseDate;
    private String paymentMethod;
    private LocalDateTime createdAt;

    // Constructor
    public Expense(int id, double amount, String description, int categoryId,
                   LocalDate expenseDate, String paymentMethod, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.categoryId = categoryId;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    // =====| Getters and setters
    // ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // General getters
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public int getCategoryId() { return categoryId; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
