/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.personalfinancetrackerctu;

/**
 *
 * @author ludwi
 */
public class Expense {
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
}