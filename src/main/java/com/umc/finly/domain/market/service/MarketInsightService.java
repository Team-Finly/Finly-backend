package com.umc.finly.domain.market.service;

import com.umc.finly.domain.market.dto.MarketInsightResDTO;
import com.umc.finly.domain.market.repository.MarketInsightRepository;
import com.umc.finly.domain.market.repository.projection.StockEmotionBuyAggregation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.umc.finly.domain.record.enums.EmotionCode;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketInsightService {

    private final MarketInsightRepository marketInsightRepository;

    // 실시간 인사이트 (자동 캐러셀 1문장씩 가져오기)
    public MarketInsightResDTO getMarketInsight() {

        LocalDate fromDate = LocalDate.now().minusDays(7);

        List<StockEmotionBuyAggregation> rows =
                marketInsightRepository.aggregateBuyEmotionByStock(fromDate);

        if (rows.isEmpty()) {//사용자 데이터가 비어있을 때 (초기 상태)
            return MarketInsightResDTO.builder()
                    .message("아직 충분한 사용자 데이터가 없어요")
                    .dominantEmotion("EMPTY")
                    .buySellRatio("EMPTY")
                    .confidenceLevel("EMPTY")
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

        // 캐러셀용 랜덤 1개 선택하는 로직
        List<StockEmotionBuyAggregation> candidates =
                new ArrayList<>(dominantByStock.values());

        StockEmotionBuyAggregation picked =
                candidates.get(new Random().nextInt(candidates.size()));

        String stockName = picked.getStockName();
        EmotionCode emotion = EmotionCode.valueOf(picked.getEmotionCode());// 감정 enum 받아오기

        String message = generateMessage(stockName, emotion);

        return MarketInsightResDTO.builder()
                .stockName(stockName)
                .message(message)
                .dominantEmotion(emotion.name())
                .buySellRatio("BUY_DOMINANT")
                .confidenceLevel(calcConfidence(candidates.size()))
                .build();
    }

    //문장 생성하기 (사용자가 구매한 종목명 표시하기  )
    private String generateMessage(String stockName, EmotionCode emotion) {
        return switch (emotion) {
            case ANXIETY ->
                    "지금 " + stockName + " 주주들은 불안해하고 있어요";

            case REGRET ->
                    "지금 " + stockName + " 주주들은 후회하는 감정을 느끼고 있어요";

            case GREED ->
                    "지금 " + stockName + " 주주들은 욕심이 커진 상태예요";

            case CONFIDENCE ->
                    "지금 " + stockName + " 주주들은 확신을 가지고 있어요";

            case CALM ->
                    "지금 " + stockName + " 주주들은 차분한 상태를 유지하고 있어요";
        };
    }

    private String calcConfidence(int sampleSize) {
        if (sampleSize >= 10) return "HIGH";
        if (sampleSize >= 5) return "MEDIUM";
        return "LOW";
    }
}
