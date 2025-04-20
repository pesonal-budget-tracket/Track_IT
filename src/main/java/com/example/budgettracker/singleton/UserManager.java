package com.example.budgettracker.singleton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.budgettracker.factory.UserFactory;
import com.example.budgettracker.model.User;

@Component
public class UserManager {
    private static UserManager instance;
    private final Map<String, User> users = new HashMap<>();
    private static final String USERS_FILE = "data/users.csv";  // make sure this folder exists

    private UserManager() {
        loadUsersFromCSV();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private void loadUsersFromCSV() {
        try {
            Path path = Paths.get(USERS_FILE);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 3) {
                        String username = data[0];
                        String password = data[1];
                        String role = data[2];
                        User user = UserFactory.createUser(username, password, role);
                        users.put(username, user);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password, String role) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }

        User newUser = UserFactory.createUser(username, password, role);
        users.put(username, newUser);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + "," + password + "," + role);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        createUserTransactionFile(username);
        return true;
    }

    private void createUserTransactionFile(String username) {
        String filename = "data/" + username + "_transactions.csv";
        try {
            File file = new File(filename);
            if (!file.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("type,amount,description,date");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
