package com.example.binance.scheduler;

import com.example.binance.config.BinanceProperties;
import com.example.binance.service.PriceCollectionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;

@Component
public class PricePollingScheduler {
    private final PriceCollectionService collectionService; private final BinanceProperties properties;
    private ThreadPoolTaskScheduler scheduler;
    public PricePollingScheduler(PriceCollectionService collectionService, BinanceProperties properties) { this.collectionService = collectionService; this.properties = properties; }
    @PostConstruct public void start() {
        scheduler = new ThreadPoolTaskScheduler(); scheduler.setPoolSize(1); scheduler.setThreadNamePrefix("binance-poller-"); scheduler.setWaitForTasksToCompleteOnShutdown(true); scheduler.initialize();
        Duration interval = properties.pollInterval();
        scheduler.scheduleWithFixedDelay(this::pollSafely, Instant.now(), interval);
    }
    private void pollSafely() {
        try { collectionService.collect(); }
        catch (Exception ex) { org.slf4j.LoggerFactory.getLogger(getClass()).error("Unexpected polling failure; scheduled execution will continue", ex); }
    }
    @PreDestroy public void stop() { if (scheduler != null) scheduler.shutdown(); }
}
