package com.example.budgettracker.factory;

import java.util.Date;

import com.example.budgettracker.model.ExpenseTransaction;
import com.example.budgettracker.model.IncomeTransaction;
import com.example.budgettracker.model.Transaction;

public class TransactionFactory {
    
    // Existing method (no category)
    public static Transaction createTransaction(String type, double amount, Date date, String description) {
        if ("income".equalsIgnoreCase(type)) {
            return new IncomeTransaction(amount, date, description);
        } else if ("expense".equalsIgnoreCase(type)) {
            return new ExpenseTransaction(amount, date, description);
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }

    // ✅ New overloaded method (with category)
    public static Transaction createTransaction(String type, double amount, Date date, String description, String category) {
        if ("income".equalsIgnoreCase(type)) {
            return new IncomeTransaction(amount, date, description, category);
        } else if ("expense".equalsIgnoreCase(type)) {
            return new ExpenseTransaction(amount, date, description, category);
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }
}
