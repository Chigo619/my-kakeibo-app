package com.example.kakeibo.controller;

import com.example.kakeibo.model.Expense;
import com.example.kakeibo.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ExpenseService expenseService;

    public HomeController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("expenseList", expenseService.findAll());
        return "home";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam String date,
            @RequestParam(required = false) Integer amount,
            @RequestParam String content,
            Model model
    ) {
        if (date == null || date.isBlank() ||
                amount == null ||
                content == null || content.isBlank()) {

            model.addAttribute("expenseList", expenseService.findAll());
            model.addAttribute("errorMessage", "日付・金額・内容は必須です。");
            return "home";
        }

        expenseService.save(date, amount, content);
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model) {
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        return "edit";
    }

    @PostMapping("/update")
    public String update(
            @RequestParam Long id,
            @RequestParam String date,
            @RequestParam(required = false) Integer amount,
            @RequestParam String content,
            Model model
    ) {
        if (date == null || date.isBlank() ||
                amount == null ||
                content == null || content.isBlank()) {

            Expense expense = expenseService.findById(id);
            expense.setDate(date);
            expense.setAmount(amount == null ? 0 : amount);
            expense.setContent(content);

            model.addAttribute("expense", expense);
            model.addAttribute("errorMessage", "日付・金額・内容は必須です。");
            return "edit";
        }

        expenseService.update(id, date, amount, content);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        expenseService.delete(id);
        return "redirect:/";
    }
}