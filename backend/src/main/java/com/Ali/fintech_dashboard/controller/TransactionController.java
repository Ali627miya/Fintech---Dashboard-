package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.dto.CategorySpendDto;
import com.Ali.fintech_dashboard.entity.Category;
import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.repository.CategoryRepository;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import com.Ali.fintech_dashboard.service.TransactionSyncService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final TransactionSyncService transactionSyncService;
    private final CategoryRepository categoryRepository;

    public TransactionController(TransactionRepository transactionRepository,
                                  TransactionSyncService transactionSyncService,
                                  CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionSyncService = transactionSyncService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/sync")
    public List<Transaction> sync() {
        return transactionSyncService.syncTransactions();
    }

    /**
     * Monthly spend aggregated by category, for the given year-month
     * (defaults to the current month if not provided).
     * Example: GET /api/transactions/summary?month=2026-09
     */
    @GetMapping("/summary")
    public List<CategorySpendDto> getMonthlySummary(
            @RequestParam(required = false) String month) {

        YearMonth targetMonth = (month != null) ? YearMonth.parse(month) : YearMonth.now();
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        List<Transaction> monthTransactions = transactionRepository.findAll().stream()
            .filter(t -> t.getCategoryId() != null)
            .filter(t -> !t.getTransactionDate().isBefore(monthStart)
                      && !t.getTransactionDate().isAfter(monthEnd))
            .toList();

        Map<Long, BigDecimal> totalsByCategory = monthTransactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategoryId,
                Collectors.reducing(BigDecimal.ZERO,
                    t -> t.getAmount().abs(),
                    BigDecimal::add)
            ));

        return totalsByCategory.entrySet().stream()
            .map(entry -> {
                Category category = categoryRepository.findById(entry.getKey()).orElse(null);
                String name = category != null ? category.getName() : "Unknown";
                return new CategorySpendDto(entry.getKey(), name, entry.getValue());
            })
            .sorted((a, b) -> b.getTotalSpent().compareTo(a.getTotalSpent()))
            .toList();
    }
}
