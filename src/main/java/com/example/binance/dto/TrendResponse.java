package com.example.binance.dto;
import java.time.LocalDate;
public record TrendResponse(String symbol, LocalDate start_date, LocalDate end_date, Trend trend) {
    public enum Trend { UPWARD, DOWNWARD, FLAT, INSUFFICIENT_DATA }
}
