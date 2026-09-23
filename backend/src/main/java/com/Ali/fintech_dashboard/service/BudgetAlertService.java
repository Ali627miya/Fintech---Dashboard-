package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.dto.BudgetStatusDto;
import com.Ali.fintech_dashboard.entity.Budget;
import com.Ali.fintech_dashboard.entity.Category;
import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.repository.BudgetRepository;
import com.Ali.fintech_dashboard.repository.CategoryRepository;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetAlertService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public BudgetAlertService(BudgetRepository budgetRepository,
                               TransactionRepository transactionRepository,
                               CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<BudgetStatusDto> getBudgetStatus(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        return budgets.stream().map(budget -> {
            Category category = categoryRepository.findById(budget.getCategoryId())
                .orElse(null);
            String categoryName = category != null ? category.getName() : "Unknown";

            BigDecimal spent = transactionRepository.findAll().stream()
                .filter(t -> budget.getCategoryId().equals(t.getCategoryId()))
                .filter(t -> !t.getTransactionDate().isBefore(monthStart)
                          && !t.getTransactionDate().isAfter(monthEnd))
                .map(t -> t.getAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal percentUsed = budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0
                ? spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                       .multiply(BigDecimal.valueOf(100))
                       .setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            boolean overBudget = spent.compareTo(budget.getMonthlyLimit()) > 0;

            return new BudgetStatusDto(
                budget.getCategoryId(),
                categoryName,
                budget.getMonthlyLimit(),
                spent,
                percentUsed,
                overBudget
            );
        }).toList();
    }
}
