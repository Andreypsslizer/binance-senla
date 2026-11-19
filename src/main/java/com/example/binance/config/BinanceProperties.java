package com.example.binance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "binance")
public record BinanceProperties(String baseUrl, String symbol, Duration pollInterval, Duration connectTimeout, Duration readTimeout) {
    public BinanceProperties {
        if (baseUrl == null || baseUrl.isBlank()) baseUrl = "https://api.binance.com";
        if (symbol == null || symbol.isBlank()) symbol = "BNBUSDT";
        if (pollInterval == null || pollInterval.isZero() || pollInterval.isNegative()) pollInterval = Duration.ofSeconds(30);
        if (connectTimeout == null) connectTimeout = Duration.ofSeconds(3);
        if (readTimeout == null) readTimeout = Duration.ofSeconds(5);
    }
}
