package com.example.budgettracker.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.service.TransactionService;
import com.example.budgettracker.singleton.BudgetManager;
import com.example.budgettracker.strategy.MonthlySummaryReportStrategy;
import com.example.budgettracker.strategy.ReportGenerator;
import com.example.budgettracker.strategy.ReportStrategy;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final BudgetManager budgetManager;

    @Autowired
    public TransactionController(TransactionService transactionService, BudgetManager budgetManager) {
        this.transactionService = transactionService;
        this.budgetManager = budgetManager;
    }
    
    @GetMapping("/transactions")
    public String getTransactions(@RequestParam String username, Model model) {
        List<Transaction> transactions = transactionService.getTransactionsForUser(username);
        double totalBudget = transactionService.calculateTotalBudget(username);

        model.addAttribute("transactions", transactions);
        model.addAttribute("username", username);
        model.addAttribute("totalBudget", totalBudget);
        return "transaction"; // 👈 match the HTML file name: transaction.html
    }
    
    @PostMapping("/addTransaction")
    public String addTransaction(@RequestParam String username,
                                @RequestParam String type,
                                @RequestParam double amount,
                                @RequestParam String description,
                                @RequestParam String category,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        // Check if it's an expense and would exceed the budget, but still allow it
        if (type.equalsIgnoreCase("expense")) {
            boolean withinBudget = transactionService.isTransactionWithinCategoryBudget(username, category, amount, date);
            
            // Instead of preventing the transaction, just add a warning
            if (!withinBudget) {
                // Save the transaction anyway
                transactionService.addTransaction(username, type, amount, java.sql.Date.valueOf(date), description, category);
                
                // Add the warning message for after redirect
                redirectAttributes.addFlashAttribute("warning", "Warning: This transaction exceeds your budget for the category: " + category);
                
                // Update category spending
                transactionService.updateCategorySpendingCSV(username, category, amount, date);
                
                return "redirect:/transactions?username=" + username;
            }
        }

        // Normal flow - save the transaction
        transactionService.addTransaction(username, type, amount, java.sql.Date.valueOf(date), description, category);

        // Update category CSV for expenses
        if (type.equalsIgnoreCase("expense")) {
            transactionService.updateCategorySpendingCSV(username, category, amount, date);
        }
        System.out.println("✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅");

        return "redirect:/transactions?username=" + username;
    }
    
    // Monthly report functionality
    @GetMapping("/transactions/monthly-report")
    public String showMonthlyReportForm(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "monthly-report";
    }
    
    @PostMapping("/transactions/generate-report")
    public String generateMonthlyReport(
            @RequestParam String username,
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            Model model) {
        
        // Get all transactions for the user
        List<Transaction> allTransactions = transactionService.getTransactionsForUser(username);
        
        // Filter transactions for the specified month and year
        List<Transaction> filteredTransactions = allTransactions.stream()
            .filter(txn -> {
                // Safely convert java.util.Date to LocalDate
                Date utilDate = txn.getDate();
                LocalDate txnDate = utilDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                
                return txnDate.getMonthValue() == month && txnDate.getYear() == year;
            })
            .collect(Collectors.toList());
        
        // Generate the report using the strategy pattern
        ReportStrategy strategy = new MonthlySummaryReportStrategy();
        ReportGenerator generator = new ReportGenerator(strategy);
        String report = generator.generate(filteredTransactions);
        
        // Add attributes to the model
        model.addAttribute("transactions", filteredTransactions);
        model.addAttribute("report", report);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("monthName", getMonthName(month));
        model.addAttribute("username", username);
        
        return "report-result";
    }
    
    private String getMonthName(int month) {
        return java.time.Month.of(month).toString();
    }
}