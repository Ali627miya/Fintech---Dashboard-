package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.entity.RecurringPayment;
import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.repository.RecurringPaymentRepository;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecurringPaymentDetectionService {

    private static final int MIN_FREQUENCY_DAYS = 27;
    private static final int MAX_FREQUENCY_DAYS = 32;
    private static final double AMOUNT_TOLERANCE_PERCENT = 0.05; // 5% variance allowed

    private final TransactionRepository transactionRepository;
    private final RecurringPaymentRepository recurringPaymentRepository;

    public RecurringPaymentDetectionService(TransactionRepository transactionRepository,
                                             RecurringPaymentRepository recurringPaymentRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
    }

    public int detectRecurringPayments() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        // Group by accountId + merchantName (null merchants are skipped, can't group meaningfully)
        Map<String, List<Transaction>> grouped = allTransactions.stream()
            .filter(t -> t.getMerchantName() != null && !t.getMerchantName().isBlank())
            .collect(Collectors.groupingBy(t -> t.getAccountId() + "|" + t.getMerchantName()));

        int detectedCount = 0;

        for (Map.Entry<String, List<Transaction>> entry : grouped.entrySet()) {
            List<Transaction> group = entry.getValue().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .toList();

            if (group.size() < 2) continue; // need at least 2 occurrences to detect a pattern

            List<Integer> gaps = new ArrayList<>();
            for (int i = 1; i < group.size(); i++) {
                long days = ChronoUnit.DAYS.between(
                    group.get(i - 1).getTransactionDate(),
                    group.get(i).getTransactionDate()
                );
                gaps.add((int) days);
            }

            boolean allGapsMonthly = gaps.stream()
                .allMatch(g -> g >= MIN_FREQUENCY_DAYS && g <= MAX_FREQUENCY_DAYS);

            if (!allGapsMonthly) continue;

            boolean amountsConsistent = amountsWithinTolerance(group);
            if (!amountsConsistent) continue;

            // It's recurring — mark all transactions in this group, and save/update the summary row
            for (Transaction t : group) {
                t.setRecurring(true);
                transactionRepository.save(t);
            }

            Transaction latest = group.get(group.size() - 1);
            BigDecimal avgAmount = group.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(group.size()), 2, RoundingMode.HALF_UP);
            int avgFrequency = (int) gaps.stream().mapToInt(Integer::intValue).average().orElse(30);

            RecurringPayment recurring = recurringPaymentRepository
                .findByAccountIdAndMerchantName(latest.getAccountId(), latest.getMerchantName())
                .orElse(new RecurringPayment());

            recurring.setAccountId(latest.getAccountId());
            recurring.setMerchantName(latest.getMerchantName());
            recurring.setAverageAmount(avgAmount);
            recurring.setFrequencyDays(avgFrequency);
            recurring.setLastSeenDate(latest.getTransactionDate());
            recurring.setNextExpectedDate(latest.getTransactionDate().plusDays(avgFrequency));

            recurringPaymentRepository.save(recurring);
            detectedCount++;
        }

        return detectedCount;
    }

    private boolean amountsWithinTolerance(List<Transaction> group) {
        BigDecimal first = group.get(0).getAmount().abs();
        for (Transaction t : group) {
            BigDecimal diff = t.getAmount().abs().subtract(first).abs();
            BigDecimal allowedVariance = first.multiply(BigDecimal.valueOf(AMOUNT_TOLERANCE_PERCENT));
            if (diff.compareTo(allowedVariance) > 0) {
                return false;
            }
        }
        return true;
    }
}
