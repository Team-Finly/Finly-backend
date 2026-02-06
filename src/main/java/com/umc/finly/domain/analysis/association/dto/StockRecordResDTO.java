package com.umc.finly.domain.analysis.association.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockRecordResDTO {
    private Long stockId; // 종목 아이디
    private String stockCode;// 종목 코드
    private String stockName; // 종목 이름

    public StockRecordResDTO(Long stockId, String stockCode, String stockName) {
        this.stockId = stockId;
        this.stockCode = stockCode;
        this.stockName = stockName;
    }
}
