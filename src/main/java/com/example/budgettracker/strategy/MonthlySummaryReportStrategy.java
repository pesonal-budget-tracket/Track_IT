package com.example.budgettracker.strategy;

import com.example.budgettracker.model.Transaction;
import java.util.List;

public class MonthlySummaryReportStrategy implements ReportStrategy {

    @Override
    public String generateReport(List<Transaction> transactions) {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction txn : transactions) {
            if (txn.getType().equalsIgnoreCase("income")) {
                totalIncome += txn.getAmount();
            } else if (txn.getType().equalsIgnoreCase("expense")) {
                totalExpense += txn.getAmount();
            }
        }

        double net = totalIncome - totalExpense;

        return String.format(
            "Monthly Summary:\n- Total Income: ₹%.2f\n- Total Expense: ₹%.2f\n- Net Savings: ₹%.2f",
            totalIncome, totalExpense, net
        );
    }
}
