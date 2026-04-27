package com.example.kakeibo.controller;

import com.example.kakeibo.model.Expense;
import com.example.kakeibo.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final ExpenseService expenseService;

    public HomeController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model
    ) {
        LocalDate today = LocalDate.now();

        int displayYear = year != null ? year : today.getYear();
        int displayMonth = month != null ? month : today.getMonthValue();

        int lastDay = YearMonth.of(displayYear, displayMonth).lengthOfMonth();
        List<Integer> days = new ArrayList<>();

        for (int day = 1; day <= lastDay; day++) {
            days.add(day);
        }

        Map<Integer, List<Expense>> calendarExpenses =
                expenseService.getCalendarExpenses(displayYear, displayMonth);

        model.addAttribute("displayYear", displayYear);
        model.addAttribute("displayMonth", displayMonth);
        model.addAttribute("days", days);
        model.addAttribute("calendarExpenses", calendarExpenses);
        model.addAttribute("groupedExpenses", expenseService.groupByDate());
        model.addAttribute("expenseList", expenseService.findAll());

        return "home";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam String date,
            @RequestParam(required = false) Integer amount,
            @RequestParam String content,
            @RequestParam String category,
            Model model
    ) {
        if (date == null || date.isBlank()
                || amount == null
                || content == null || content.isBlank()
                || category == null || category.isBlank()) {

            LocalDate today = LocalDate.now();
            int displayYear = today.getYear();
            int displayMonth = today.getMonthValue();

            int lastDay = YearMonth.of(displayYear, displayMonth).lengthOfMonth();
            List<Integer> days = new ArrayList<>();

            for (int day = 1; day <= lastDay; day++) {
                days.add(day);
            }

            model.addAttribute("displayYear", displayYear);
            model.addAttribute("displayMonth", displayMonth);
            model.addAttribute("days", days);
            model.addAttribute("calendarExpenses", expenseService.getCalendarExpenses(displayYear, displayMonth));
            model.addAttribute("groupedExpenses", expenseService.groupByDate());
            model.addAttribute("expenseList", expenseService.findAll());
            model.addAttribute("errorMessage", "日付・金額・内容・カテゴリは必須です。");

            return "home";
        }

        expenseService.save(date, amount, content, category);
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
            @RequestParam String category,
            Model model
    ) {
        if (date == null || date.isBlank()
                || amount == null
                || content == null || content.isBlank()
                || category == null || category.isBlank()) {

            Expense expense = expenseService.findById(id);
            expense.setDate(date);
            expense.setAmount(amount == null ? 0 : amount);
            expense.setContent(content);
            expense.setCategory(category);

            model.addAttribute("expense", expense);
            model.addAttribute("errorMessage", "日付・金額・内容・カテゴリは必須です。");

            return "edit";
        }

        expenseService.update(id, date, amount, content, category);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        expenseService.delete(id);
        return "redirect:/";
    }
}