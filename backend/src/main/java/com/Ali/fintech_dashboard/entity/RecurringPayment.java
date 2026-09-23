package com.Ali.fintech_dashboard.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_payments")
public class RecurringPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "average_amount", nullable = false)
    private BigDecimal averageAmount;

    @Column(name = "frequency_days", nullable = false)
    private Integer frequencyDays;

    @Column(name = "last_seen_date", nullable = false)
    private LocalDate lastSeenDate;

    @Column(name = "next_expected_date")
    private LocalDate nextExpectedDate;

    public RecurringPayment() {}

    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public BigDecimal getAverageAmount() { return averageAmount; }
    public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }
    public Integer getFrequencyDays() { return frequencyDays; }
    public void setFrequencyDays(Integer frequencyDays) { this.frequencyDays = frequencyDays; }
    public LocalDate getLastSeenDate() { return lastSeenDate; }
    public void setLastSeenDate(LocalDate lastSeenDate) { this.lastSeenDate = lastSeenDate; }
    public LocalDate getNextExpectedDate() { return nextExpectedDate; }
    public void setNextExpectedDate(LocalDate nextExpectedDate) { this.nextExpectedDate = nextExpectedDate; }
}
