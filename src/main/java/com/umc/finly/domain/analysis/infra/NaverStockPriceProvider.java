package com.umc.finly.domain.analysis.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.finly.domain.analysis.exception.code.AnalysisErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// 네이버 금융 API를 이용해 국내 주식의 현재가를 조회
@Component
public class NaverStockPriceProvider implements StockPriceProvider {
    private static final String NAVER_API_URL = "https://polling.finance.naver.com/api/realtime/domestic/stock/%s";
    private final ObjectMapper objectMapper; // JSON 문자열을 자바 객체로 변환해주는 객체
    private final RestClient restClient; // 외부 HTTP API를 호출하는 클라이언트

    public NaverStockPriceProvider(ObjectMapper objectMapper,
                                   @Qualifier("naverStockRestClient") RestClient restClient) {
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

    @Override
    public Integer getCurrentPrice(String stockCode) {
        String url = String.format(NAVER_API_URL, stockCode);

        try {
            String response = restClient.get()
                    .uri(url)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                    .header("Accept", "application/json")
                    .retrieve() // HTTP 요청 전송
                    .body(String.class); // HTTP 응답 중 Body 부분만 꺼내서 String 타입으로 변환

            JsonNode root = objectMapper.readTree(response);
            JsonNode stockSnapshots = root.get("datas");

            if (stockSnapshots == null || !stockSnapshots.isArray()) { // 네이버 주식 API 응답에 주식 데이터가 없음
                throw new CustomException(AnalysisErrorCode.STOCK_CURRENT_PRICE_RESPONSE_INVALID);
            }

            if (stockSnapshots.isEmpty()) { // 존재하지 않는 종목 코드에 대한 요청인 경우
                throw new CustomException(AnalysisErrorCode.STOCK_CURRENT_PRICE_NOT_FOUND);
            }

            JsonNode stock = stockSnapshots.get(0);
            JsonNode closePriceRaw = stock.get("closePriceRaw");

            if (closePriceRaw == null) { // closePriceRaw 데이터가 없음
                throw new CustomException(AnalysisErrorCode.STOCK_CURRENT_PRICE_RESPONSE_INVALID);
            }

            return closePriceRaw.asInt();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(AnalysisErrorCode.STOCK_CURRENT_PRICE_API_FAILED);
        }
    }
}
