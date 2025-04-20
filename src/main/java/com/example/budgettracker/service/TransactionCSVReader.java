package com.example.budgettracker.service;

import java.io.BufferedReader; // import ConcreteTransaction
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.budgettracker.model.ConcreteTransaction;

public class TransactionCSVReader {

    public static List<ConcreteTransaction> readTransactionsForMonth(String filePath, int month, int year) {
        List<ConcreteTransaction> filtered = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                LocalDate date = LocalDate.parse(parts[0]); // format: yyyy-MM-dd
                String type = parts[1];
                double amount = Double.parseDouble(parts[2]);
                String category = parts[3];

                if (date.getMonthValue() == month && date.getYear() == year) {
                    // Instantiate ConcreteTransaction instead of Transaction
                    filtered.add(new ConcreteTransaction(amount, java.sql.Date.valueOf(date), type, category));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filtered;
    }
}
