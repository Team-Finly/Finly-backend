package com.umc.finly.domain.market.stock.infra;

import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * TradingView 심볼 페이지 HTML을 가져오는 클라이언트.
 * ex) symbol=005930 → GET {baseUrl}/KRX-005930/
 */
@Component
@RequiredArgsConstructor
public class TradingViewSymbolClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${tradingview.symbol-base-url}")
    private String symbolBaseUrl; // ex) https://kr.tradingview.com/symbols/

    public String fetchHtmlForKrSymbol(String symbol) {
        // 공통 포맷: {base}/KRX-{symbol}/
        String url = String.format("%s%sKRX-%s/",
                symbolBaseUrl.endsWith("/") ? symbolBaseUrl : symbolBaseUrl + "/",
                "",
                symbol
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0") // 간단 UA 지정
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            if (statusCode == 404) {
                // Case 1: 미등록 종목 (404)
                throw new StockInfoException(
                        StockInfoErrorCode.TRADINGVIEW_SYMBOL_NOT_FOUND,
                        String.format("TradingView 미등록 종목 (Symbol: %s, URL: %s)", symbol, url)
                );
            }

            if (statusCode != 200) {
                // Case 2: 기타 에러
                throw new StockInfoException(
                        StockInfoErrorCode.TRADINGVIEW_REQUEST_FAILED,
                        String.format("TradingView HTML 호출 실패 (Status: %d, URL: %s)", statusCode, url)
                );
            }

            return response.body();

        } catch (StockInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new StockInfoException(
                    StockInfoErrorCode.TRADINGVIEW_REQUEST_FAILED,
                    "TradingView HTML 호출 중 시스템 예외 발생 (URL: " + url + ")",
                    e
            );
        }
    }
}

