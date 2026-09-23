package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.config.TrueLayerProperties;
import com.Ali.fintech_dashboard.dto.TrueLayerListResponse;
import com.Ali.fintech_dashboard.dto.TrueLayerTransactionDto;
import com.Ali.fintech_dashboard.entity.Account;
import com.Ali.fintech_dashboard.entity.Transaction;
import com.Ali.fintech_dashboard.entity.UserToken;
import com.Ali.fintech_dashboard.repository.AccountRepository;
import com.Ali.fintech_dashboard.repository.TransactionRepository;
import com.Ali.fintech_dashboard.repository.UserTokenRepository;
import com.Ali.fintech_dashboard.security.TokenEncryptionUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TransactionSyncService {

    private final TrueLayerProperties props;
    private final UserTokenRepository userTokenRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TokenEncryptionUtil encryptionUtil;
    private final TrueLayerAuthService trueLayerAuthService;
    private final RestClient restClient = RestClient.create();

    public TransactionSyncService(TrueLayerProperties props,
                                   UserTokenRepository userTokenRepository,
                                   AccountRepository accountRepository,
                                   TransactionRepository transactionRepository,
                                   TokenEncryptionUtil encryptionUtil,
                                   TrueLayerAuthService trueLayerAuthService) {
        this.props = props;
        this.userTokenRepository = userTokenRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.encryptionUtil = encryptionUtil;
        this.trueLayerAuthService = trueLayerAuthService;
    }

    public List<Transaction> syncTransactions() {
        UserToken userToken = userTokenRepository.findAll().stream()
            .filter(t -> "truelayer".equals(t.getProvider()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No TrueLayer connection found. Connect a bank account first."));

        String accessToken;
        if (userToken.getExpiresAt().isBefore(LocalDateTime.now().plusSeconds(30))) {
            accessToken = trueLayerAuthService.refreshAccessToken(userToken);
        } else {
            accessToken = encryptionUtil.decrypt(userToken.getAccessTokenEncrypted());
        }

        List<Account> accounts = accountRepository.findByUserId(userToken.getUserId());

        for (Account account : accounts) {
            TrueLayerListResponse<TrueLayerTransactionDto> response = restClient.get()
                .uri(props.getApiBaseUrl() + "/data/v1/accounts/" + account.getTrueLayerAccountId() + "/transactions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<TrueLayerListResponse<TrueLayerTransactionDto>>() {});

            if (response == null || response.getResults() == null) {
                continue;
            }

            for (TrueLayerTransactionDto dto : response.getResults()) {
                Transaction transaction = transactionRepository
                    .findByTrueLayerTransactionId(dto.getTransactionId())
                    .orElse(new Transaction());

                transaction.setAccountId(account.getId());
                transaction.setTrueLayerTransactionId(dto.getTransactionId());
                transaction.setAmount(dto.getAmount());
                transaction.setCurrency(dto.getCurrency());
                transaction.setDescription(dto.getDescription());
                transaction.setMerchantName(dto.getMerchantName());
                transaction.setTransactionType(dto.getTransactionType());

                if (dto.getTimestamp() != null) {
                    LocalDate date = OffsetDateTime.parse(dto.getTimestamp()).toLocalDate();
                    transaction.setTransactionDate(date);
                }

                transactionRepository.save(transaction);
            }
        }

        return transactionRepository.findAll();
    }
}
