package com.example.budgettracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // this resolves to register.html in templates
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // this resolves to login.html
    }
}
