package com.example.binance.dto;
import com.example.binance.entity.PriceEntity;
import java.math.BigDecimal;
import java.time.Instant;
public record PriceResponse(String symbol, BigDecimal price, Instant fetchedAt) {
    public static PriceResponse from(PriceEntity entity) { return new PriceResponse(entity.getSymbol(), entity.getPrice(), entity.getFetchedAt()); }
}
