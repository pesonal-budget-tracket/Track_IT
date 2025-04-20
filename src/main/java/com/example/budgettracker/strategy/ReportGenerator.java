package com.example.budgettracker.strategy;

import java.util.List;

import com.example.budgettracker.model.Transaction;

public class ReportGenerator {
    private ReportStrategy strategy;

    public ReportGenerator(ReportStrategy strategy) {
        this.strategy = strategy;
    }

    public String generate(List<Transaction> transactions) {
        return strategy.generateReport(transactions);
    }

    public void setStrategy(ReportStrategy strategy) {
        this.strategy = strategy;
    }
}
