package com.umc.finly.global.config;



import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record AppCorsProperties(String allowedOrigins) {

    public List<String> originList() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) return List.of();
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}