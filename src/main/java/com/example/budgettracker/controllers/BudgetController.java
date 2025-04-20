package com.example.budgettracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.budgettracker.singleton.BudgetManager;

@Controller
public class BudgetController {

    private final BudgetManager budgetManager;

    public BudgetController(BudgetManager budgetManager) {
        this.budgetManager = budgetManager;
    }

    @GetMapping("/budget")
    public String getBudget(@RequestParam String username, Model model) {
        model.addAttribute("budget", budgetManager.getUserBudget(username));
        model.addAttribute("username", username); // ✅ Needed for consistent navigation in Thymeleaf
        return "budget";
    }
}
