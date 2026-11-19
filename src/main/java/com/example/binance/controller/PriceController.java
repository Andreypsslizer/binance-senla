package com.example.binance.controller;

import com.example.binance.dto.*;
import com.example.binance.service.PriceQueryService;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController @RequestMapping("/api/v1/prices") @Validated
public class PriceController {
    private final PriceQueryService service;
    public PriceController(PriceQueryService service) { this.service = service; }
    @GetMapping("/latest") public PriceResponse latest() { return service.latest(); }
    @GetMapping("/average") public AveragePriceResponse average(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) { validate(from, to); return service.average(from, to); }
    @GetMapping public PricePageResponse page(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to, @RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "20") @Min(1) int size) { validate(from, to); if (size > 500) throw new IllegalArgumentException("size must not exceed 500"); return service.page(from, to, page, size); }
    @GetMapping("/trend") public TrendResponse trend(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) { validate(from, to); return service.trend(from, to); }
    private void validate(Instant from, Instant to) { if (from.isAfter(to)) throw new IllegalArgumentException("from must be less than or equal to to"); }
}
