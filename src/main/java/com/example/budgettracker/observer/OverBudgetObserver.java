package com.example.budgettracker.observer;

import java.util.ArrayList;
import java.util.List;

import com.example.budgettracker.model.CategoryBudget;

/**
 * Another concrete observer that tracks overbudget categories
 * for different reporting or notification purposes
 */
public class OverBudgetObserver implements BudgetObserver {
    private List<CategoryBudget> overBudgetCategories = new ArrayList<>();
    
    @Override
    public void update(CategoryBudget budget) {
        if (budget.getSpent() > budget.getAmount()) {
            // Only add if it's not already in our list
            if (!overBudgetCategories.contains(budget)) {
                overBudgetCategories.add(budget);
            }
        }
    }

    /**
     * Get all categories that are over budget
     * @return List of over-budget categories
     */
    public List<CategoryBudget> getOverBudgetCategories() {
        return overBudgetCategories;
    }
    
    /**
     * Clear the list of over-budget categories
     */
    public void clearOverBudgetCategories() {
        overBudgetCategories.clear();
    }
}