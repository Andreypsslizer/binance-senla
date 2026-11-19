package com.example.binance.client;

import com.example.binance.config.BinanceProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestClient;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class BinanceClientTest {
 static WireMockServer wireMock;
 @BeforeAll static void start() { wireMock = new WireMockServer(0); wireMock.start(); }
 @AfterAll static void stop() { wireMock.stop(); }
 @BeforeEach void reset() { wireMock.resetAll(); }
 @Test void readsAveragePriceFromBinanceApi() {
  wireMock.stubFor(get(urlPathEqualTo("/api/v3/avgPrice")).withQueryParam("symbol", equalTo("BNBUSDT")).willReturn(okJson("{\"mins\":5,\"price\":\"612.34000000\",\"closeTime\":1790000000000}")));
  var client = new BinanceClient(RestClient.builder().baseUrl(wireMock.baseUrl()).build(), new BinanceProperties(wireMock.baseUrl(),"BNBUSDT",null,null,null));
  assertEquals("612.34000000", client.getAveragePrice().toPlainString());
 }
}
