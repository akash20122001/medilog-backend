package com.medilog.com.medilog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    
    private String secret = "G9Xxw2W7f9p3r5S9n7b+2vK8qZy1d8n5uXo2q3s4t5u6v7w8x9y0zA==";
    private long expirationMs = 864000000L;
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getExpirationMs() {
        return expirationMs;
    }
    
    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}