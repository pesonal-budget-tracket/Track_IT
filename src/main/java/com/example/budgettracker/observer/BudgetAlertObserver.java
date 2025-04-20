package com.example.budgettracker.observer;

import java.util.ArrayList;
import java.util.List;

import com.example.budgettracker.model.CategoryBudget;

/**
 * Concrete observer that creates alert messages when budgets are exceeded
 */
public class BudgetAlertObserver implements BudgetObserver {
    private List<String> alerts = new ArrayList<>();
    
    @Override
    public void update(CategoryBudget budget) {
        double amountOver = budget.getSpent() - budget.getAmount();
        String alertMessage = String.format(
            "ALERT: Your %s budget is over by $%.2f (%.1f%% of budget used)",
            budget.getCategory(),
            amountOver,
            budget.getPercentageUsed()
        );
        alerts.add(alertMessage);
    }
    
    /**
     * Get all collected alerts
     * @return List of alert messages
     */
    public List<String> getAlerts() {
        return alerts;
    }
    
    /**
     * Clear all alerts
     */
    public void clearAlerts() {
        alerts.clear();
    }
}