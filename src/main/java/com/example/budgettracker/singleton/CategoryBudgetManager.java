package com.example.budgettracker.singleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.budgettracker.model.CategoryBudget;

@Component
public class CategoryBudgetManager {

    public List<CategoryBudget> getBudgetsForUser(String username) {
        List<CategoryBudget> userBudgets = new ArrayList<>();

        String filePath = "data/" + username + "_category_budgets.csv";
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String category = parts[0];
                        double amount = Double.parseDouble(parts[1]);
                        YearMonth month = YearMonth.parse(parts[2]);

                        CategoryBudget budget = new CategoryBudget(username, category, amount, month);
                        userBudgets.add(budget);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading category budgets for " + username + ": " + e.getMessage());
            }
        }

        return userBudgets;
    }

    public CategoryBudget getBudget(String username, String category, YearMonth month) {
        List<CategoryBudget> budgets = getBudgetsForUser(username);
        for (CategoryBudget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category) && b.getMonth().equals(month)) {
                return b;
            }
        }
        return null;
    }

    public void updateBudget(String username, CategoryBudget updatedBudget) {
        List<CategoryBudget> budgets = getBudgetsForUser(username);
        boolean updated = false;

        for (int i = 0; i < budgets.size(); i++) {
            CategoryBudget b = budgets.get(i);
            if (b.getCategory().equalsIgnoreCase(updatedBudget.getCategory()) &&
                b.getMonth().equals(updatedBudget.getMonth())) {
                budgets.set(i, updatedBudget); // Replace with updated
                updated = true;
                break;
            }
        }

        if (updated) {
            saveAllBudgets(username, budgets); // Rewrite full CSV
        } else {
            addBudget(username, updatedBudget); // If not found, just add
        }
    }

    public void addBudget(String username, CategoryBudget budget) {
        String filePath = "data/" + username + "_category_budgets.csv";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(budget.getCategory() + "," +
                         budget.getAmount() + "," +
                         budget.getMonth().toString() + "\n");
        } catch (IOException e) {
            System.err.println("Error saving category budget: " + e.getMessage());
        }
    }

    private void saveAllBudgets(String username, List<CategoryBudget> budgets) {
        String filePath = "data/" + username + "_category_budgets.csv";
        try (FileWriter writer = new FileWriter(filePath)) {
            for (CategoryBudget budget : budgets) {
                writer.write(budget.getCategory() + "," +
                             budget.getAmount() + "," +
                             budget.getMonth().toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error rewriting category budget file: " + e.getMessage());
        }
    }

    public List<CategoryBudget> getBudgets(String username) {
        return getBudgetsForUser(username);
    }
}
