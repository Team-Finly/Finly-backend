package com.umc.finly.domain.analysis.stock.converter;


import com.umc.finly.domain.analysis.stock.dto.response.RecentDecisionResDTO;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RecentDecisionConverter {
    private RecentDecisionConverter() {}

    public static RecentDecisionResDTO toDto(
            Stock stock,
            RecordEntry record,
            BigDecimal accumulatedBuyAmount
    ) {
        BigDecimal sellAmount =
                record.getUnitPrice().multiply(record.getQuantity());

        int decisionResult = sellAmount
                .subtract(accumulatedBuyAmount)
                .multiply(BigDecimal.valueOf(100))
                .divide(accumulatedBuyAmount, 0, RoundingMode.HALF_UP)
                .intValue();

        return RecentDecisionResDTO.builder()
                .stockName(stock.getName())
                .emotion(record.getEmotionCode().name())
                .tradeType("매도")
                .price(record.getUnitPrice().intValue())
                .date(record.getRecordDate())
                .quantity(record.getQuantity().intValue())
                .decisionResult(decisionResult)
                .build();
    }
}
