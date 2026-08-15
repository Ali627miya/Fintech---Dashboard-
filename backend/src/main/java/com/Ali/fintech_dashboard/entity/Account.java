package com.Ali.fintech_dashboard.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "truelayer_account_id", nullable = false)
    private String trueLayerAccountId;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_type")
    private String accountType;

    private String currency;

    private BigDecimal balance;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    public Account() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTrueLayerAccountId() { return trueLayerAccountId; }
    public void setTrueLayerAccountId(String v) { this.trueLayerAccountId = v; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public LocalDateTime getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(LocalDateTime lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }
}
