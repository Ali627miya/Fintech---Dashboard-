package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.service.CategorizationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class CategorizationController {

    private final CategorizationService categorizationService;

    public CategorizationController(CategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    @PostMapping("/categorize")
    public Map<String, Integer> categorize() {
        int count = categorizationService.categorizeUncategorizedTransactions();
        return Map.of("categorized", count);
    }
}
