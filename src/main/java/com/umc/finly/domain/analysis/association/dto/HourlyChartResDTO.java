package com.umc.finly.domain.analysis.association.dto;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record HourlyChartResDTO(
        Long stockId,
        String stockCode,
        String stockName,
        String now,
        LocalDate targetDate,
        List<PriceData> prices,
        List<RecordData> records
) {

    public record PriceData(
            String dateTime, // yyyy-MM-dd HH:mm 형식
            BigDecimal price
    ) {}


    @Builder
    public record RecordData(
            Long recordId,
            LocalDate recordDate,
            EmotionCode emotionCode,
            Integer emotionIntensity,
            TradeAction tradeAction,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice,
            String memo,
            String finlyTalk // record_feedback.content
    ) {}
}
