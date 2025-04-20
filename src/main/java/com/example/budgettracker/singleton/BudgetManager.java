package com.example.budgettracker.singleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class BudgetManager {

    public double getUserBudget(String username) {
        String filePath = "data/" + username + "_budget.txt";
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextDouble()) {
                    double budget = scanner.nextDouble();
                    scanner.close();
                    return budget;
                }
                scanner.close();
            } else {
                // Create a new budget file with 0.0 balance
                setUserBudget(username, 0.0);
            }
        } catch (Exception e) {
            System.err.println("Error reading budget for " + username + ": " + e.getMessage());
        }
        return 0.0;
    }

    public void setUserBudget(String username, double amount) {
        String filePath = "data/" + username + "_budget.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(amount);
        } catch (IOException e) {
            System.err.println("Error writing budget for " + username + ": " + e.getMessage());
        }
    }

    public void addToBudget(String username, double amount) {
        double current = getUserBudget(username);
        setUserBudget(username, current + amount);
    }

    public void deductFromBudget(String username, double amount) {
        double current = getUserBudget(username);
        setUserBudget(username, current - amount);
    }
}
 