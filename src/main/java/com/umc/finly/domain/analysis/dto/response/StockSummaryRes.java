package com.umc.finly.domain.analysis.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSummaryRes {
    private Integer averageBuyPrice;
    private Integer currentPrice; // currentPrice 응답 필드만 우선 연결
    private Integer totalBuyCount;
    private Integer maxHoldingDays;

    public static StockSummaryRes currentPriceSummary(Integer currentPrice) {
        return StockSummaryRes.builder()
                .currentPrice(currentPrice)
                .build();
    }
}
