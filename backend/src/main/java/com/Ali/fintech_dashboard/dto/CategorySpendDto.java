package com.Ali.fintech_dashboard.dto;

import java.math.BigDecimal;

public class CategorySpendDto {
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalSpent;

    public CategorySpendDto(Long categoryId, String categoryName, BigDecimal totalSpent) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalSpent = totalSpent;
    }

    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getTotalSpent() { return totalSpent; }
}
