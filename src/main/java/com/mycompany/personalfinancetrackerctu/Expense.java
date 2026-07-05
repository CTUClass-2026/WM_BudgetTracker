/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;

/**
 *
 * @author ludwi
 */
/*public class Expense {
    private final String date;
    private final String category;
    private final double amount;
    private final String description;
    
    public Expense(String date, String category, double amount, String description){
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
    public String getDate() { return date;}
    public String getCategory() { return category;}
    public double getAmount() { return amount;}
    public String getDescription() { return description;}
}*/

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Expense {
    private int id;
    private double amount;
    private String description;
    private int categoryId;
    private LocalDate expenseDate;
    private String paymentMethod;
    private LocalDateTime createdAt;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }   

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}