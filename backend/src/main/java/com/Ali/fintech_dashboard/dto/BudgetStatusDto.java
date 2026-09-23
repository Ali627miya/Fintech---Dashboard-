package com.Ali.fintech_dashboard.dto;

import java.math.BigDecimal;

public class BudgetStatusDto {
    private Long categoryId;
    private String categoryName;
    private BigDecimal monthlyLimit;
    private BigDecimal spentSoFar;
    private BigDecimal percentUsed;
    private boolean overBudget;

    public BudgetStatusDto(Long categoryId, String categoryName, BigDecimal monthlyLimit,
                            BigDecimal spentSoFar, BigDecimal percentUsed, boolean overBudget) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.monthlyLimit = monthlyLimit;
        this.spentSoFar = spentSoFar;
        this.percentUsed = percentUsed;
        this.overBudget = overBudget;
    }

    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public BigDecimal getSpentSoFar() { return spentSoFar; }
    public BigDecimal getPercentUsed() { return percentUsed; }
    public boolean isOverBudget() { return overBudget; }
}
