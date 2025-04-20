package com.example.budgettracker.factory;

import com.example.budgettracker.model.User;

public class UserFactory {
    public static User createUser(String username, String password, String role) {
        return new User(username, password, role);
    }
}
