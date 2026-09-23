package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import com.Ali.fintech_dashboard.service.TransactionSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final TransactionSyncService transactionSyncService;

    public TransactionController(TransactionRepository transactionRepository,
                                  TransactionSyncService transactionSyncService) {
        this.transactionRepository = transactionRepository;
        this.transactionSyncService = transactionSyncService;
    }

    @GetMapping
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/sync")
    public List<Transaction> sync() {
        return transactionSyncService.syncTransactions();
    }
}
