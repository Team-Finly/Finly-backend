package com.umc.finly.domain.market.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.finly.domain.market.dto.FearGreedResult;
import com.umc.finly.domain.market.enums.FearGreedStatus;
import com.umc.finly.domain.market.exception.code.MarketErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

@Component
public class CnnFearGreedIndexProvider implements FearGreedIndexProvider {
    private static final String CNN_API_URL = "https://production.dataviz.cnn.io/index/fearandgreed/graphdata/%s";
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public CnnFearGreedIndexProvider(ObjectMapper objectMapper,
                                     @Qualifier("marketRestClient") RestClient restClient) {
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

    @Override
    public FearGreedResult getFearGreedIndex() {
        try {
            // CNN은 미국 기준 날짜 사용
            String date = LocalDate.now(ZoneId.of("America/New_York"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US));

            String response = restClient.get()
                    .uri(String.format(CNN_API_URL, date))
                    .header("Content-Type", "application/json")
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36")
                    .header("Referer", "https://edition.cnn.com/")
                    .header("Origin", "https://edition.cnn.com")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode data = root.get("fear_and_greed");

            if (data == null || data.isNull()) {
                throw new CustomException(MarketErrorCode.MARKET_INDEX_RESPONSE_INVALID);
            }

            JsonNode scoreNode = data.get("score");

            if (scoreNode == null || scoreNode.isNull()) {
                throw new CustomException(MarketErrorCode.MARKET_INDEX_RESPONSE_INVALID);
            }

            // 반올림
            double rawScore = scoreNode.asDouble();
            int score = (int) Math.round(rawScore);

            JsonNode ratingNode = data.get("rating");

            if (ratingNode == null || ratingNode.isNull()) {
                throw new CustomException(MarketErrorCode.MARKET_INDEX_RESPONSE_INVALID);
            }

            FearGreedStatus status = FearGreedStatus.getFearGreedStatus(ratingNode.asText());

            return new FearGreedResult(score, status);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(MarketErrorCode.MARKET_INDEX_API_FAILED);
        }
    }
}
