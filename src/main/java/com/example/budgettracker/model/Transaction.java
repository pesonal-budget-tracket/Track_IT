package com.example.budgettracker.model;

import java.util.Date;

public abstract class Transaction {
    protected double amount;
    protected Date date;
    protected String description;
    protected String category;

    public Transaction(double amount, Date date, String description) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = "";
    }

    public Transaction(double amount, Date date, String description, String category) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public abstract String getDetails();

    // ✅ Add this:
    public abstract String getType();
}
