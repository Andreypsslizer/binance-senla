package com.example.binance.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record BinanceAvgPriceResponse(String mins, BigDecimal price, @JsonProperty("closeTime") Long closeTime) {}
