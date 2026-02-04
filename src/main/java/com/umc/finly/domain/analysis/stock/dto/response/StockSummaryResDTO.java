package com.umc.finly.domain.analysis.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSummaryResDTO {
    private Integer averageBuyPrice;
    private Integer currentPrice;
    private Integer totalBuyCount;
    private Integer maxHoldingDays;
}
