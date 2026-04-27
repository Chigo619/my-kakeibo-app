package com.example.kakeibo.service;

import com.example.kakeibo.model.Expense;
import com.example.kakeibo.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Map<String, List<Expense>> groupByDate() {
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getDate() != null && !expense.getDate().isBlank())
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.groupingBy(
                        Expense::getDate,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public Map<Integer, List<Expense>> getCalendarExpenses(int year, int month) {
        List<Expense> allExpenses = expenseRepository.findAll();

        Map<Integer, List<Expense>> calendarMap = new HashMap<>();

        for (Expense expense : allExpenses) {
            if (expense.getDate() == null || expense.getDate().isBlank()) {
                continue;
            }

            LocalDate date = LocalDate.parse(expense.getDate());

            if (date.getYear() == year && date.getMonthValue() == month) {
                int day = date.getDayOfMonth();

                calendarMap
                        .computeIfAbsent(day, key -> new ArrayList<>())
                        .add(expense);
            }
        }

        return calendarMap;
    }

    public void save(String date, int amount, String content, String category) {
        Expense expense = new Expense(date, amount, content, category);
        expenseRepository.save(expense);
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id).orElseThrow();
    }

    public void update(Long id, String date, int amount, String content, String category) {
        Expense expense = expenseRepository.findById(id).orElseThrow();
        expense.setDate(date);
        expense.setAmount(amount);
        expense.setContent(content);
        expense.setCategory(category);
        expenseRepository.save(expense);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }
}