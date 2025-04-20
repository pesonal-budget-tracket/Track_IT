package com.example.budgettracker.strategy;

import java.util.List;

import com.example.budgettracker.model.Transaction;

public interface ReportStrategy {
    String generateReport(List<Transaction> transactions);
}
