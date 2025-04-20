package com.example.budgettracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.budgettracker.model.User;
import com.example.budgettracker.singleton.UserManager;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserManager userManager = UserManager.getInstance();

    @PostMapping("/register")
    public String register(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String role) {
        if (userManager.registerUser(username, password, role)) {
            // Redirect to login page with a success message (optional)
            return "redirect:/login?registered=true";
        } else {
            // Redirect to registration page with an error
            return "redirect:/register?error=usernameTaken";
        }
    }


    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        User user = userManager.authenticateUser(username, password);
        if (user != null) {
            // ✅ Redirect to transaction page with username
            return "redirect:/transactions?username=" + username;
        } else {
            // ❌ Return to login form with error (assuming you have a login.html)
            return "redirect:/login?error=true";
        }
    }
}
