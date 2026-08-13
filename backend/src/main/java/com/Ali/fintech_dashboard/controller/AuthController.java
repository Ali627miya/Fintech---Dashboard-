package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.config.TrueLayerProperties;
import com.Ali.fintech_dashboard.service.TrueLayerAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class AuthController {

    private final TrueLayerProperties trueLayerProperties;
    private final TrueLayerAuthService trueLayerAuthService;

    public AuthController(TrueLayerProperties trueLayerProperties,
                           TrueLayerAuthService trueLayerAuthService) {
        this.trueLayerProperties = trueLayerProperties;
        this.trueLayerAuthService = trueLayerAuthService;
    }

    @GetMapping("/api/auth/connect")
    public String connect() {
        return UriComponentsBuilder
            .fromUriString(trueLayerProperties.getAuthBaseUrl() + "/")
            .queryParam("response_type", "code")
            .queryParam("client_id", trueLayerProperties.getClientId())
            .queryParam("redirect_uri", trueLayerProperties.getRedirectUri())
            .queryParam("scope", "info accounts balance transactions offline_access")
            .queryParam("providers", "uk-cs-mock uk-ob-all uk-oauth-all")
            .build()
            .toUriString();
    }

    @GetMapping("/api/auth/callback")
    public String callback(@RequestParam(required = false) String code,
                            @RequestParam(required = false) String error) {
        if (error != null) {
            return "Authorization failed: " + error;
        }
        if (code == null) {
            return "No authorization code received.";
        }

        trueLayerAuthService.exchangeCodeForTokens(code);
        return "Bank account connected successfully! Tokens stored securely.";
    }
}
