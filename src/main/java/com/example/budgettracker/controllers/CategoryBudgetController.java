package com.example.budgettracker.controllers;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.budgettracker.model.CategoryBudget;
import com.example.budgettracker.observer.BudgetAlertObserver;
import com.example.budgettracker.observer.BudgetNotifier;
import com.example.budgettracker.observer.OverBudgetObserver;
import com.example.budgettracker.service.TransactionService;
import com.example.budgettracker.singleton.CategoryBudgetManager;


@Controller
@RequestMapping("/category-budgets")
public class CategoryBudgetController {

    private final CategoryBudgetManager manager;
    private final TransactionService transactionService;
    private final BudgetNotifier budgetNotifier;
    private final BudgetAlertObserver alertObserver;
    private final OverBudgetObserver overBudgetObserver;

    @Autowired
    public CategoryBudgetController(CategoryBudgetManager manager, TransactionService transactionService) {
        this.manager = manager;
        this.transactionService = transactionService;
        
        // Initialize the observer pattern components
        this.budgetNotifier = new BudgetNotifier();
        this.alertObserver = new BudgetAlertObserver();
        this.overBudgetObserver = new OverBudgetObserver();
        
        // Register observers with the notifier
        this.budgetNotifier.addObserver(alertObserver);
        this.budgetNotifier.addObserver(overBudgetObserver);
    }

    @GetMapping("/form")
    public String showBudgetForm(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "set_category_budget"; // set_category_budget.html (form)
    }

    @PostMapping("/add")
    public String addCategoryBudget(@RequestParam String username,
                                    @RequestParam String category,
                                    @RequestParam double amount,
                                    @RequestParam("month") String monthStr) {

        YearMonth month = YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        CategoryBudget existingBudget = manager.getBudget(username, category, month);

        if (existingBudget != null) {
            existingBudget.setAmount(amount); // Update
            manager.updateBudget(username, existingBudget);
        } else {
            CategoryBudget newBudget = new CategoryBudget(username, category, amount, month);
            manager.addBudget(username, newBudget);
        }

        return "redirect:/category-budgets/view?username=" + username;
    }

    @GetMapping
    public String defaultRedirect(@RequestParam String username) {
        return "redirect:/category-budgets/view?username=" + username;
    }

    @GetMapping("/view")
    public String showBudgets(@RequestParam String username, Model model) {
        List<CategoryBudget> budgets = manager.getBudgetsForUser(username);

        // Clear previous alerts to avoid duplicates
        alertObserver.clearAlerts();
        overBudgetObserver.clearOverBudgetCategories();
        
        for (CategoryBudget budget : budgets) {
            // Calculate spending information
            double spent = transactionService.getSpentFromCSV(username, budget.getCategory(), budget.getMonth());
            double percentageUsed = (spent / budget.getAmount()) * 100;

            // Update budget with spending data
            budget.setSpent(spent);
            budget.setPercentageUsed(percentageUsed);
            budget.setPercentageLabel(String.format("%.2f", percentageUsed));

            // Check if over budget and notify observers if necessary
            if (spent > budget.getAmount()) {
                budgetNotifier.notifyObservers(budget);
            }
        }

        // Add data to the model for the view
        model.addAttribute("username", username);
        model.addAttribute("categoryBudgets", budgets);
        model.addAttribute("alerts", alertObserver.getAlerts());
        model.addAttribute("overBudgetCategories", overBudgetObserver.getOverBudgetCategories());
        
        return "category-budgets";
    }
}