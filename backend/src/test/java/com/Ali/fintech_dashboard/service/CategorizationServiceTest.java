package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.entity.Category;
import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.repository.CategoryRepository;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorizationServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategorizationService categorizationService;

    @Test
    void categorizesGroceryTransactionByMerchantName() {
        Category groceries = new Category();
        groceries.setName("Groceries");
        ReflectionTestUtils.setField(groceries, "id", 1L);

        Transaction tescoTransaction = new Transaction();
        tescoTransaction.setMerchantName("TESCO STORES 1234");
        tescoTransaction.setDescription("Card payment");
        tescoTransaction.setAmount(BigDecimal.valueOf(-25.50));
        tescoTransaction.setTransactionDate(LocalDate.now());

        when(transactionRepository.findAll()).thenReturn(List.of(tescoTransaction));
        when(categoryRepository.findAll()).thenReturn(List.of(groceries));

        int categorizedCount = categorizationService.categorizeUncategorizedTransactions();

        assertEquals(1, categorizedCount);
        assertEquals(1L, tescoTransaction.getCategoryId());
        verify(transactionRepository, times(1)).save(tescoTransaction);
    }

    @Test
    void skipsTransactionsThatAlreadyHaveACategory() {
        Transaction alreadyCategorized = new Transaction();
        alreadyCategorized.setCategoryId(5L);
        alreadyCategorized.setMerchantName("TESCO STORES");

        when(transactionRepository.findAll()).thenReturn(List.of(alreadyCategorized));

        int categorizedCount = categorizationService.categorizeUncategorizedTransactions();

        assertEquals(0, categorizedCount);
        verify(transactionRepository, never()).save(any());
    }
}
