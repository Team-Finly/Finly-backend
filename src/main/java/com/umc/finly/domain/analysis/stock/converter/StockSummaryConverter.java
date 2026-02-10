package com.umc.finly.domain.analysis.stock.converter;

import com.umc.finly.domain.analysis.stock.dto.res.StockSummaryResDTO;

public class StockSummaryConverter {
    private StockSummaryConverter() {}

    public static StockSummaryResDTO toDto(
            int averageBuyPrice,
            int currentPrice,
            int totalBuyCount,
            int maxHoldingDays
    ) {
        return StockSummaryResDTO.builder()
                .averageBuyPrice(averageBuyPrice)
                .currentPrice(currentPrice)
                .totalBuyCount(totalBuyCount)
                .maxHoldingDays(maxHoldingDays)
                .build();
    }
}
