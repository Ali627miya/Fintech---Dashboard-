package com.Ali.fintech_dashboard.repository;

import com.Ali.fintech_dashboard.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
    Optional<Account> findByTrueLayerAccountId(String trueLayerAccountId);
}
