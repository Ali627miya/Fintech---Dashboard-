package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.entity.Category;
import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.repository.CategoryRepository;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategorizationService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    // Keyword -> category name. Checked against merchant_name (case-insensitive, substring match).
    private static final Map<String, String> KEYWORD_RULES = Map.ofEntries(
        Map.entry("tesco", "Groceries"),
        Map.entry("sainsbury", "Groceries"),
        Map.entry("aldi", "Groceries"),
        Map.entry("asda", "Groceries"),
        Map.entry("lidl", "Groceries"),
        Map.entry("uber eats", "Dining"),
        Map.entry("deliveroo", "Dining"),
        Map.entry("just eat", "Dining"),
        Map.entry("mcdonald", "Dining"),
        Map.entry("costa", "Dining"),
        Map.entry("starbucks", "Dining"),
        Map.entry("uber", "Transport"),
        Map.entry("tfl", "Transport"),
        Map.entry("trainline", "Transport"),
        Map.entry("shell", "Transport"),
        Map.entry("bp ", "Transport"),
        Map.entry("netflix", "Subscriptions"),
        Map.entry("spotify", "Subscriptions"),
        Map.entry("amazon prime", "Subscriptions"),
        Map.entry("disney", "Subscriptions"),
        Map.entry("amazon", "Shopping"),
        Map.entry("asos", "Shopping"),
        Map.entry("zara", "Shopping"),
        Map.entry("cinema", "Entertainment"),
        Map.entry("vue", "Entertainment"),
        Map.entry("odeon", "Entertainment"),
        Map.entry("council tax", "Bills"),
        Map.entry("british gas", "Bills"),
        Map.entry("thames water", "Bills"),
        Map.entry("ee ", "Bills"),
        Map.entry("vodafone", "Bills"),
        Map.entry("salary", "Income"),
        Map.entry("payroll", "Income")
    );

    public CategorizationService(CategoryRepository categoryRepository,
                                  TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Categorizes all transactions that don't yet have a category assigned.
     * Returns the number of transactions that were successfully categorized.
     */
    public int categorizeUncategorizedTransactions() {
        List<Transaction> uncategorized = transactionRepository.findAll().stream()
            .filter(t -> t.getCategoryId() == null)
            .toList();

        int count = 0;
        for (Transaction transaction : uncategorized) {
            Optional<Long> categoryId = matchCategory(transaction);
            if (categoryId.isPresent()) {
                transaction.setCategoryId(categoryId.get());
                transactionRepository.save(transaction);
                count++;
            }
        }
        return count;
    }

    private Optional<Long> matchCategory(Transaction transaction) {
        String merchant = transaction.getMerchantName();
        String description = transaction.getDescription();
        String searchText = ((merchant != null ? merchant : "") + " " +
                              (description != null ? description : "")).toLowerCase();

        for (Map.Entry<String, String> rule : KEYWORD_RULES.entrySet()) {
            if (searchText.contains(rule.getKey())) {
                return categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(rule.getValue()))
                    .map(Category::getId)
                    .findFirst();
            }
        }

        // Fallback: uncategorized transactions default to "Other"
        return categoryRepository.findAll().stream()
            .filter(c -> c.getName().equalsIgnoreCase("Other"))
            .map(Category::getId)
            .findFirst();
    }
}
