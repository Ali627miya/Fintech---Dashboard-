package com.Ali.fintech_dashboard.service;

import com.Ali.fintech_dashboard.config.TrueLayerProperties;
import com.Ali.fintech_dashboard.dto.TokenResponse;
import com.Ali.fintech_dashboard.entity.User;
import com.Ali.fintech_dashboard.entity.UserToken;
import com.Ali.fintech_dashboard.repository.UserRepository;
import com.Ali.fintech_dashboard.repository.UserTokenRepository;
import com.Ali.fintech_dashboard.security.TokenEncryptionUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

@Service
public class TrueLayerAuthService {

    private final TrueLayerProperties props;
    private final TokenEncryptionUtil encryptionUtil;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final RestClient restClient = RestClient.create();

    public TrueLayerAuthService(TrueLayerProperties props,
                                 TokenEncryptionUtil encryptionUtil,
                                 UserRepository userRepository,
                                 UserTokenRepository userTokenRepository) {
        this.props = props;
        this.encryptionUtil = encryptionUtil;
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
    }

    public void exchangeCodeForTokens(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", props.getClientId());
        form.add("client_secret", props.getClientSecret());
        form.add("redirect_uri", props.getRedirectUri());
        form.add("code", code);

        TokenResponse response = restClient.post()
            .uri(props.getAuthBaseUrl() + "/connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .body(form)
            .retrieve()
            .body(TokenResponse.class);

        if (response == null || response.getAccessToken() == null) {
            throw new RuntimeException("Token exchange failed: empty response from TrueLayer");
        }

        User user = userRepository.findAll().stream().findFirst()
            .orElseGet(() -> userRepository.save(new User("default-user@example.com")));

        UserToken token = new UserToken();
        token.setUserId(user.getId());
        token.setProvider("truelayer");
        token.setAccessTokenEncrypted(encryptionUtil.encrypt(response.getAccessToken()));
        token.setRefreshTokenEncrypted(encryptionUtil.encrypt(response.getRefreshToken()));
        token.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()));

        userTokenRepository.save(token);
    }
}
