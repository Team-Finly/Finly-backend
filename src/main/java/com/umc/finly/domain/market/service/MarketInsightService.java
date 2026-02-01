package com.umc.finly.domain.market.service;

import com.umc.finly.domain.market.dto.MarketInsightResponse;
import com.umc.finly.domain.market.repository.MarketInsightRepository;
import com.umc.finly.domain.market.repository.projection.StockEmotionBuyAggregation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketInsightService {

    private final MarketInsightRepository marketInsightRepository;

    // 실시간 인사이트 (자동 캐러셀 1문장씩 가져오기)
    public MarketInsightResponse getMarketInsight() {

        LocalDate fromDate = LocalDate.now().minusDays(7);

        List<StockEmotionBuyAggregation> rows =
                marketInsightRepository.aggregateBuyEmotionByStock(fromDate);

        if (rows.isEmpty()) {//사용자 데이터가 비어있을 때
            return MarketInsightResponse.builder()
                    .message("아직 충분한 사용자 데이터가 없어요")
                    .dominantEmotion("NEUTRAL")
                    .buySellRatio("BALANCED")
                    .confidenceLevel("LOW")
                    .build();
        }

        // 종목별로 가장 많이 나온 감정 하나씩 집계하기
        Map<Long, StockEmotionBuyAggregation> dominantByStock =
                rows.stream()
                        .collect(Collectors.groupingBy(
                                StockEmotionBuyAggregation::getStockId,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(
                                                Comparator.comparingLong(
                                                        StockEmotionBuyAggregation::getBuyCount
                                                )
                                        ),
                                        Optional::get
                                )
                        ));

        // 캐러셀용 랜덤 1개 선택
        List<StockEmotionBuyAggregation> candidates =
                new ArrayList<>(dominantByStock.values());

        StockEmotionBuyAggregation picked =
                candidates.get(new Random().nextInt(candidates.size()));

        String stockName = picked.getStockName();
        String emotion   = picked.getEmotionCode(); // enum name

        String message = generateMessage(stockName, emotion);

        return MarketInsightResponse.builder()
                .message(message)
                .dominantEmotion(emotion)
                .buySellRatio("BUY_DOMINANT")
                .confidenceLevel(calcConfidence(candidates.size()))
                .build();
    }

    //문장 생성하기 (사용자가 구매한 종목명 표시하기  )
    private String generateMessage(String stockName, String emotion) {
        return switch (emotion) {
            case "FEAR" ->
                    "지금 서비스 유저들은 불안할 때 " + stockName + "을(를) 매수했어요";
            case "EXPECTATION" ->
                    "기대감이 높을 때 " + stockName + "을(를) 산 유저가 많아요";
            case "GREED" ->
                    "강한 확신 속에서 " + stockName + " 매수가 이뤄졌어요";
            default ->
                    "차분한 심리에서 " + stockName + " 매수가 이뤄졌어요";
        };
    }

    private String calcConfidence(int sampleSize) {
        if (sampleSize >= 10) return "HIGH";
        if (sampleSize >= 5) return "MEDIUM";
        return "LOW";
    }
}
