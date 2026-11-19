package com.example.binance.service;

import com.example.binance.client.BinanceClient;
import com.example.binance.config.BinanceProperties;
import com.example.binance.entity.PriceEntity;
import com.example.binance.repository.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;

@Service
public class PriceCollectionService {
    private static final Logger log = LoggerFactory.getLogger(PriceCollectionService.class);
    private final BinanceClient client; private final PriceRepository repository; private final BinanceProperties properties; private final Clock clock;
    public PriceCollectionService(BinanceClient client, PriceRepository repository, BinanceProperties properties, Clock clock) {
        this.client = client; this.repository = repository; this.properties = properties; this.clock = clock;
    }
    @Transactional
    public void collect() {
        try {
            var price = client.getAveragePrice();
            var fetchedAt = Instant.now(clock);
            repository.save(new PriceEntity(properties.symbol(), price, fetchedAt));
            log.info("Stored Binance average price: symbol={}, price={}, fetchedAt={}", properties.symbol(), price, fetchedAt);
        } catch (Exception ex) {
            log.error("Price polling failed; next scheduled attempt will continue", ex);
        }
    }
}
