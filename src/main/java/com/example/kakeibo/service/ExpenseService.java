package com.example.kakeibo.service;

import com.example.kakeibo.model.Expense;
import com.example.kakeibo.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public void save(String date, int amount, String content) {
        Expense expense = new Expense(date, amount, content);
        expenseRepository.save(expense);
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id).orElseThrow();
    }

    public void update(Long id, String date, int amount, String content) {
        Expense expense = expenseRepository.findById(id).orElseThrow();
        expense.setDate(date);
        expense.setAmount(amount);
        expense.setContent(content);
        expenseRepository.save(expense);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }
}