package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.dto.BudgetStatusDto;
import com.Ali.fintech_dashboard.entity.Budget;
import com.Ali.fintech_dashboard.repository.BudgetRepository;
import com.Ali.fintech_dashboard.repository.UserRepository;
import com.Ali.fintech_dashboard.service.BudgetAlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final BudgetAlertService budgetAlertService;

    public BudgetController(BudgetRepository budgetRepository,
                             UserRepository userRepository,
                             BudgetAlertService budgetAlertService) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.budgetAlertService = budgetAlertService;
    }

    @GetMapping
    public List<Budget> getBudgets() {
        return budgetRepository.findAll();
    }

    @PostMapping
    public Budget createOrUpdateBudget(@RequestBody Map<String, Object> body) {
        Long categoryId = Long.valueOf(body.get("categoryId").toString());
        java.math.BigDecimal limit = new java.math.BigDecimal(body.get("monthlyLimit").toString());

        Long userId = userRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No user found"))
            .getId();

        Budget budget = budgetRepository.findByUserIdAndCategoryId(userId, categoryId)
            .orElse(new Budget());
        budget.setUserId(userId);
        budget.setCategoryId(categoryId);
        budget.setMonthlyLimit(limit);

        return budgetRepository.save(budget);
    }

    @GetMapping("/alerts")
    public List<BudgetStatusDto> getAlerts() {
        Long userId = userRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No user found"))
            .getId();
        return budgetAlertService.getBudgetStatus(userId);
    }
}
