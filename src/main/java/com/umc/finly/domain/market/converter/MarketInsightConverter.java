package com.umc.finly.domain.market.converter;

import com.umc.finly.domain.market.dto.response.MarketInsightResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;

public class MarketInsightConverter {

    private MarketInsightConverter() {
    }

    // 초기 상태 (데이터 없음)
    public static MarketInsightResDTO toEmptyInsight() {
        return MarketInsightResDTO.builder()
                .message("아직 충분한 사용자 데이터가 없어요")
                .dominantEmotion("EMPTY")
                .buySellRatio("EMPTY")
                .confidenceLevel("EMPTY")
                .build();
    }

    // 정상 인사이트 응답
    public static MarketInsightResDTO toInsight(
            String stockName,
            String message,
            EmotionCode dominantEmotion,
            String buySellRatio,
            String confidenceLevel
    ) {
        return MarketInsightResDTO.builder()
                .stockName(stockName)
                .message(message)
                .dominantEmotion(dominantEmotion.name())
                .buySellRatio(buySellRatio)
                .confidenceLevel(confidenceLevel)
                .build();
    }
}
