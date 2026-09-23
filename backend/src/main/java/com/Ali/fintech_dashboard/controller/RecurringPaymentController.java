package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.entity.RecurringPayment;
import com.Ali.fintech_dashboard.repository.RecurringPaymentRepository;
import com.Ali.fintech_dashboard.service.RecurringPaymentDetectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recurring-payments")
public class RecurringPaymentController {

    private final RecurringPaymentRepository recurringPaymentRepository;
    private final RecurringPaymentDetectionService detectionService;

    public RecurringPaymentController(RecurringPaymentRepository recurringPaymentRepository,
                                       RecurringPaymentDetectionService detectionService) {
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.detectionService = detectionService;
    }

    @GetMapping
    public List<RecurringPayment> getRecurringPayments() {
        return recurringPaymentRepository.findAll();
    }

    @PostMapping("/detect")
    public Map<String, Integer> detect() {
        int count = detectionService.detectRecurringPayments();
        return Map.of("detected", count);
    }
}
