package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.config.TrueLayerProperties;
import com.Ali.fintech_dashboard.dto.TrueLayerAccountDto;
import com.Ali.fintech_dashboard.dto.TrueLayerBalanceDto;
import com.Ali.fintech_dashboard.dto.TrueLayerListResponse;
import com.Ali.fintech_dashboard.entity.Account;
import com.Ali.fintech_dashboard.entity.UserToken;
import com.Ali.fintech_dashboard.repository.AccountRepository;
import com.Ali.fintech_dashboard.repository.UserTokenRepository;
import com.Ali.fintech_dashboard.security.TokenEncryptionUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountSyncService {

    private final TrueLayerProperties props;
    private final UserTokenRepository userTokenRepository;
    private final AccountRepository accountRepository;
    private final TokenEncryptionUtil encryptionUtil;
    private final TrueLayerAuthService trueLayerAuthService;
    private final RestClient restClient = RestClient.create();

    public AccountSyncService(TrueLayerProperties props,
                               UserTokenRepository userTokenRepository,
                               AccountRepository accountRepository,
                               TokenEncryptionUtil encryptionUtil,
                               TrueLayerAuthService trueLayerAuthService) {
        this.props = props;
        this.userTokenRepository = userTokenRepository;
        this.accountRepository = accountRepository;
        this.encryptionUtil = encryptionUtil;
        this.trueLayerAuthService = trueLayerAuthService;
    }

    public List<Account> syncAccounts() {
        UserToken userToken = userTokenRepository.findAll().stream()
            .filter(t -> "truelayer".equals(t.getProvider()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No TrueLayer connection found. Connect a bank account first."));

        String accessToken;
        // Refresh proactively if the token is already expired or expiring within the next 30 seconds
        if (userToken.getExpiresAt().isBefore(LocalDateTime.now().plusSeconds(30))) {
            accessToken = trueLayerAuthService.refreshAccessToken(userToken);
        } else {
            accessToken = encryptionUtil.decrypt(userToken.getAccessTokenEncrypted());
        }

        TrueLayerListResponse<TrueLayerAccountDto> response = restClient.get()
            .uri(props.getApiBaseUrl() + "/data/v1/accounts")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .body(new org.springframework.core.ParameterizedTypeReference<TrueLayerListResponse<TrueLayerAccountDto>>() {});

        if (response == null || response.getResults() == null) {
            throw new RuntimeException("No accounts returned from TrueLayer.");
        }

        for (TrueLayerAccountDto dto : response.getResults()) {
            Account account = accountRepository.findByTrueLayerAccountId(dto.getAccountId())
                .orElse(new Account());

            account.setUserId(userToken.getUserId());
            account.setTrueLayerAccountId(dto.getAccountId());
            account.setAccountName(dto.getDisplayName());
            account.setAccountType(dto.getAccountType());
            account.setCurrency(dto.getCurrency());
            account.setLastSyncedAt(LocalDateTime.now());

            TrueLayerListResponse<TrueLayerBalanceDto> balanceResponse = restClient.get()
                .uri(props.getApiBaseUrl() + "/data/v1/accounts/" + dto.getAccountId() + "/balance")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<TrueLayerListResponse<TrueLayerBalanceDto>>() {});

            if (balanceResponse != null && balanceResponse.getResults() != null && !balanceResponse.getResults().isEmpty()) {
                account.setBalance(balanceResponse.getResults().get(0).getCurrent());
            }

            accountRepository.save(account);
        }

        return accountRepository.findByUserId(userToken.getUserId());
    }
}
