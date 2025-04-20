package com.example.budgettracker.observer;

import com.example.budgettracker.model.CategoryBudget;
import java.util.ArrayList;
import java.util.List;

/**
 * The BudgetNotifier acts as the subject in the Observer pattern.
 * It maintains a list of observers and notifies them when a budget threshold is crossed.
 */
public class BudgetNotifier {
    private List<BudgetObserver> observers = new ArrayList<>();
    
    /**
     * Adds an observer to the notification list
     * @param observer The observer to add
     */
    public void addObserver(BudgetObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Removes an observer from the notification list
     * @param observer The observer to remove
     */
    public void removeObserver(BudgetObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all registered observers about a budget update
     * @param budget The budget that has changed
     */
    public void notifyObservers(CategoryBudget budget) {
        for (BudgetObserver observer : observers) {
            observer.update(budget);
        }
    }
    
    /**
     * Checks if a budget is over its limit and notifies observers if it is
     * @param budget The budget to check
     * @return true if over budget, false otherwise
     */
    public boolean notifyIfOverBudget(CategoryBudget budget) {
        if (budget.getSpent() > budget.getAmount()) {
            notifyObservers(budget);
            return true;
        }
        return false;
    }
}