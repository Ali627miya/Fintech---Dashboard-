package com.Ali.fintech_dashboard.repository;

import com.Ali.fintech_dashboard.entity.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long> {
    Optional<RecurringPayment> findByAccountIdAndMerchantName(Long accountId, String merchantName);
    List<RecurringPayment> findByAccountId(Long accountId);
}
