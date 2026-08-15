package com.Ali.fintech_dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "truelayer")
public class TrueLayerProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authBaseUrl;
    private String apiBaseUrl;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public String getAuthBaseUrl() { return authBaseUrl; }
    public void setAuthBaseUrl(String authBaseUrl) { this.authBaseUrl = authBaseUrl; }

    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
}
