package com.example.budgettracker.model;

import java.util.Date;

public class ConcreteTransaction extends Transaction {

    public ConcreteTransaction(double amount, Date date, String description) {
        super(amount, date, description);
    }

    public ConcreteTransaction(double amount, Date date, String description, String category) {
        super(amount, date, description, category);
    }

    // Implementing the abstract method getType
    @Override
    public String getType() {
        return "ConcreteTransaction";  // You can change this to return the actual type if needed
    }

    // Implementing the abstract method getDetails
    @Override
    public String getDetails() {
        return "Amount: " + getAmount() + ", Description: " + getDescription() + ", Category: " + getCategory();
    }
}
