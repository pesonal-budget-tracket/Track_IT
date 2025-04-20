package com.example.budgettracker.observer;

import com.example.budgettracker.model.CategoryBudget;

public interface BudgetObserver {
    void update(CategoryBudget budget);
}