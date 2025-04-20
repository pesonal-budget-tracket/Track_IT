package com.example.budgettracker.model;

import java.util.Date;

public class IncomeTransaction extends Transaction {

    public IncomeTransaction(double amount, Date date, String description) {
        super(amount, date, description);
    }

    public IncomeTransaction(double amount, Date date, String description, String category) {
        super(amount, date, description, category);
    }

    @Override
    public String getDetails() {
        return String.format("[Income] Amount: $%.2f, Date: %s, Description: %s", 
                              amount, date.toString(), description);
    }

    @Override
    public String getType() {
        return "income";
    }
}
