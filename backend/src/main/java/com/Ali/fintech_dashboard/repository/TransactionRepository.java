package com.Ali.fintech_dashboard.repository;

import com.Ali.fintech_dashboard.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    Optional<Transaction> findByTrueLayerTransactionId(String trueLayerTransactionId);
}
