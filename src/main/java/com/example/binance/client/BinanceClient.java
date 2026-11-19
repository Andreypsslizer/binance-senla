package com.example.binance.client;

import com.example.binance.config.BinanceProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.math.BigDecimal;

@Component
public class BinanceClient {
    private final RestClient restClient;
    private final BinanceProperties properties;
    public BinanceClient(RestClient binanceRestClient, BinanceProperties properties) {
        this.restClient = binanceRestClient; this.properties = properties;
    }
    public BigDecimal getAveragePrice() {
        try {
            BinanceAvgPriceResponse response = restClient.get().uri(uri -> uri.path("/api/v3/avgPrice").queryParam("symbol", properties.symbol()).build())
                    .retrieve().onStatus(HttpStatusCode::isError, (request, result) -> {
                        throw new BinanceApiException(result.getStatusCode().value(), result.getHeaders().getFirst("Retry-After"));
                    }).body(BinanceAvgPriceResponse.class);
            if (response == null || response.price() == null || response.price().signum() <= 0) {
                throw new BinanceApiException("Binance returned an empty or invalid average price");
            }
            return response.price();
        } catch (RestClientResponseException ex) {
            throw new BinanceApiException(ex.getStatusCode().value(), ex.getResponseHeaders() == null ? null : ex.getResponseHeaders().getFirst("Retry-After"));
        }
    }
}
