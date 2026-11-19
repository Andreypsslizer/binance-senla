package com.example.binance.dto;
import java.util.List;
public record PricePageResponse(List<PriceResponse> items, int page, int size, long totalElements, boolean hasNext) {}
