package com.umc.finly.domain.market.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.finly.domain.market.exception.code.MarketErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class NaverMarketIndexProvider implements MarketIndexProvider {
    private static final String NAVER_API_URL = "https://m.stock.naver.com/front-api/realTime/domestic/index";
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Override
    public BigDecimal getKospi() {
        return getIndexValue("KOSPI");
    }

    @Override
    public BigDecimal getKosdaq() {
        return getIndexValue("KOSDAQ");
    }

    private BigDecimal getIndexValue(String key) {
        try {
            String response = restClient.post()
                    .uri(NAVER_API_URL)
                    .header("Content-Type", "application/json")
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                    .header("Referer", "https://m.stock.naver.com/")
                    .header("Origin", "https://m.stock.naver.com")
                    .body("""
                        {
                          "itemCodes": ["KOSPI", "KOSDAQ"]
                        }
                    """)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode marketSnapshots = root.at("/result/datas");

            if (marketSnapshots.isMissingNode()) {
                throw new CustomException(MarketErrorCode.MARKET_INDEX_RESPONSE_INVALID);
            }

            JsonNode indexNode = marketSnapshots.get(key);

            if (indexNode == null) {
                throw new CustomException(MarketErrorCode.MARKET_INDEX_NOT_FOUND);
            }

            return new BigDecimal(indexNode.get("closePriceRaw").asText());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(MarketErrorCode.MARKET_INDEX_API_FAILED);
        }
    }
}
