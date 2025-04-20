package com.example.budgettracker.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.budgettracker.factory.TransactionFactory;
import com.example.budgettracker.model.CategoryBudget;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.singleton.BudgetManager;
import com.example.budgettracker.singleton.CategoryBudgetManager;

@Service
public class TransactionService {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final BudgetManager budgetManager;
    private final CategoryBudgetManager categoryBudgetManager;

    public TransactionService(BudgetManager budgetManager, CategoryBudgetManager categoryBudgetManager) {
        this.budgetManager = budgetManager;
        this.categoryBudgetManager = categoryBudgetManager;
    }

    public void addTransaction(String username, String type, double amount, Date date, String description, String category) {
        Transaction transaction = TransactionFactory.createTransaction(type, amount, date, description, category);
        saveTransactionToCSV(username, transaction);

        if (type.equalsIgnoreCase("income")) {
            budgetManager.addToBudget(username, amount);
        } else if (type.equalsIgnoreCase("expense")) {
            budgetManager.deductFromBudget(username, amount);
        }
    }

    public boolean isTransactionWithinCategoryBudget(String username, String category, double amount, LocalDate date) {
        YearMonth month = YearMonth.from(date);
        List<CategoryBudget> budgets = categoryBudgetManager.getBudgetsForUser(username);

        for (CategoryBudget budget : budgets) {
            if (budget.getCategory().equalsIgnoreCase(category) && budget.getMonth().equals(month)) {
                double alreadySpent = calculateSpentInCategory(username, category, month);
                return (alreadySpent + amount) <= budget.getAmount();
            }
        }

        return true; // No category budget set → allow by default
    }


    public double calculateSpentInCategory(String username, String category, YearMonth month) {
        List<Transaction> transactions = getTransactionsForUser(username);
        double total = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equalsIgnoreCase(category) &&
                YearMonth.from(transaction.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()).equals(month)) {

                if (transaction.getClass().getSimpleName().equalsIgnoreCase("Expense")) {
                    total += transaction.getAmount();
                }
            }
        }

        return total;
    }

    public List<Transaction> getTransactionsForUser(String username) {
        String csvFile = "data/" + username + "_transactions.csv";
        List<Transaction> transactions = new ArrayList<>();

        Path path = Paths.get(csvFile);
        if (!Files.exists(path)) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue;

                String type = data[0];
                double amount = Double.parseDouble(data[1]);
                Date date = DATE_FORMAT.parse(data[3]);
                String description = data[2];

                Transaction transaction = TransactionFactory.createTransaction(type.toLowerCase(), amount, date, description);
                transactions.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }
    public void updateCategorySpendingCSV(String username, String category, double amount, LocalDate date) {
        String fileName = "data/spending_" + username + ".csv";
        YearMonth month = YearMonth.from(date);
    
        File file = new File(fileName);
        List<String[]> rows = new ArrayList<>();
        boolean updated = false;
    
        // Add header first
        rows.add(new String[] {"Category", "Month", "AmountSpent"});
    
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine(); // Skip header
    
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    String cat = data[0];
                    String mon = data[1];
                    double spent = Double.parseDouble(data[2]);
    
                    if (cat.equalsIgnoreCase(category) && mon.equals(month.toString())) {
                        spent += amount;
                        updated = true;
                    }
    
                    rows.add(new String[] {cat, mon, String.format("%.2f", spent)});
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        // If not updated, add new row
        if (!updated) {
            rows.add(new String[] {
                category,
                month.toString(),
                String.format("%.2f", amount)
            });
        }
    
        // Write everything back
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public double getSpentFromCSV(String username, String category, YearMonth month) {
        String fileName = "data/spending_" + username + ".csv";
        File file = new File(fileName);
    
        if (!file.exists()) {
            return 0.0;
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String csvCategory = data[0];
                String csvMonth = data[1];
                double csvSpent = Double.parseDouble(data[2]);
    
                if (csvCategory.equalsIgnoreCase(category) && csvMonth.equals(month.toString())) {
                    return csvSpent;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    
        return 0.0;
    }
    


    public double calculateTotalBudget(String username) {
        return budgetManager.getUserBudget(username);
    }

    private void saveTransactionToCSV(String username, Transaction transaction) {
        String csvFile = "data/" + username + "_transactions.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            String formattedDate = DATE_FORMAT.format(transaction.getDate());
    
            // ✅ Extract only "income" or "expense" from class name
            String type = transaction.getClass().getSimpleName().toLowerCase().replace("transaction", "");
    
            writer.write(type + "," +
                         transaction.getAmount() + "," +
                         transaction.getDescription() + "," +
                         formattedDate + "," +
                         transaction.getCategory());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Transaction> getTransactionsForMonthAndYear(String username, int month, int year) {
        // Filter the user's transactions by month and year
        return getTransactionsForUser(username).stream()
            .filter(txn -> {
                LocalDate txnDate = ((java.sql.Date) txn.getDate()).toLocalDate();
                return txnDate.getMonthValue() == month && txnDate.getYear() == year;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
}
