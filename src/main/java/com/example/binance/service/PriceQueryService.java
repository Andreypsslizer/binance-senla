package com.example.binance.service;

import com.example.binance.config.BinanceProperties;
import com.example.binance.dto.*;
import com.example.binance.entity.PriceEntity;
import com.example.binance.repository.PriceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PriceQueryService {
    private final PriceRepository repository; private final BinanceProperties properties;
    public PriceQueryService(PriceRepository repository, BinanceProperties properties) { this.repository = repository; this.properties = properties; }
    public PriceResponse latest() { return repository.findTopBySymbolOrderByFetchedAtDescIdDesc(properties.symbol()).map(PriceResponse::from).orElseThrow(() -> new PriceNotFoundException("No price data has been collected yet")); }
    public AveragePriceResponse average(Instant from, Instant to) {
        BigDecimal average = repository.averagePrice(properties.symbol(), from, to).orElseThrow(() -> new PriceNotFoundException("No price data in requested interval"));
        return new AveragePriceResponse(average.round(MathContext.DECIMAL128));
    }
    public PricePageResponse page(Instant from, Instant to, int page, int size) {
        var result = repository.findBySymbolAndFetchedAtGreaterThanEqualAndFetchedAtLessThanEqualOrderByFetchedAtDescIdDesc(properties.symbol(), from, to, PageRequest.of(page, size));
        return new PricePageResponse(result.getContent().stream().map(PriceResponse::from).toList(), page, size, result.getTotalElements(), result.hasNext());
    }
    public TrendResponse trend(Instant from, Instant to) {
        List<PriceEntity> prices = repository.findPeriod(properties.symbol(), from, to);
        TrendResponse.Trend trend = TrendResponse.Trend.INSUFFICIENT_DATA;
        if (prices.size() >= 2) {
            int comparison = prices.get(prices.size() - 1).getPrice().compareTo(prices.get(0).getPrice());
            trend = comparison > 0 ? TrendResponse.Trend.UPWARD : comparison < 0 ? TrendResponse.Trend.DOWNWARD : TrendResponse.Trend.FLAT;
        }
        return new TrendResponse(properties.symbol(), from.atZone(ZoneOffset.UTC).toLocalDate(), to.atZone(ZoneOffset.UTC).toLocalDate(), trend);
    }
}
