package com.umc.finly.domain.analysis.association.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalysisStockResDTO {
    private Long stockId; // 종목 아이디
    private String symbol;// 종목 코드
    private String stockName; // 종목 이름

    public AnalysisStockResDTO(Long stockId, String symbol, String stockName) {
        this.stockId = stockId;
        this.symbol = symbol;
        this.stockName = stockName;
    }
}
