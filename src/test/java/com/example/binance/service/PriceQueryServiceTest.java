package com.example.binance.service;

import com.example.binance.config.BinanceProperties;
import com.example.binance.entity.PriceEntity;
import com.example.binance.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceQueryServiceTest {
 @Test void trendIsUpwardWhenLastPriceExceedsFirst() {
  PriceRepository repo = mock(PriceRepository.class);
  when(repo.findPeriod(eq("BNBUSDT"), any(), any())).thenReturn(List.of(new PriceEntity("BNBUSDT", new BigDecimal("600"), Instant.parse("2026-09-01T00:00:00Z")), new PriceEntity("BNBUSDT", new BigDecimal("610"), Instant.parse("2026-09-30T00:00:00Z"))));
  var service = new PriceQueryService(repo, new BinanceProperties(null,"BNBUSDT",null,null,null));
  assertEquals("UPWARD", service.trend(Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-09-30T00:00:00Z")).trend().name());
 }
 @Test void averageReturnsNotFoundWhenNoSamples() {
  PriceRepository repo = mock(PriceRepository.class); when(repo.averagePrice(eq("BNBUSDT"), any(), any())).thenReturn(Optional.empty());
  var service = new PriceQueryService(repo, new BinanceProperties(null,"BNBUSDT",null,null,null));
  assertThrows(PriceNotFoundException.class, () -> service.average(Instant.EPOCH, Instant.now()));
 }
}
