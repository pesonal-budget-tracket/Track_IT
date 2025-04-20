package com.example.budgettracker.model;

import java.time.YearMonth;

public class CategoryBudget {
    private String username;
    private String category;
    private double amount;
    private YearMonth month; // Represents a specific month and year (e.g. 2025-04)
    private double spent;
    private double percentageUsed;
    private String percentageLabel;

    public CategoryBudget(String username, String category, double amount, YearMonth month) {
        this.username = username;
        this.category = category;
        this.amount = amount;
        this.month = month;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }
    public double getSpent() {
        return spent;
    }
    
    public void setSpent(double spent) {
        this.spent = spent;
    }
    
    public double getPercentageUsed() {
        return percentageUsed;
    }
    
    public void setPercentageUsed(double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }
    
    public String getPercentageLabel() {
        return percentageLabel;
    }
    
    public void setPercentageLabel(String percentageLabel) {
        this.percentageLabel = percentageLabel;
    }

}
