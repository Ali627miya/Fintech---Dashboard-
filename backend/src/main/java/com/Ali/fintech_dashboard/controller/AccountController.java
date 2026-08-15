package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.entity.Account;
import com.Ali.fintech_dashboard.repository.AccountRepository;
import com.Ali.fintech_dashboard.service.AccountSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountSyncService accountSyncService;

    public AccountController(AccountRepository accountRepository, AccountSyncService accountSyncService) {
        this.accountRepository = accountRepository;
        this.accountSyncService = accountSyncService;
    }

    @GetMapping
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/sync")
    public List<Account> sync() {
        return accountSyncService.syncAccounts();
    }
}
