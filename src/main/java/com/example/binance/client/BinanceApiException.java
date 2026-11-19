package com.example.binance.client;

public class BinanceApiException extends RuntimeException {
    private final int statusCode;
    private final String retryAfter;
    public BinanceApiException(String message) { super(message); this.statusCode = 0; this.retryAfter = null; }
    public BinanceApiException(int statusCode, String retryAfter) { super("Binance API returned HTTP " + statusCode); this.statusCode = statusCode; this.retryAfter = retryAfter; }
    public int getStatusCode() { return statusCode; }
    public String getRetryAfter() { return retryAfter; }
}
