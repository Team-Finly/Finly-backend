package com.umc.finly.domain.market.stock.dto;

import com.umc.finly.domain.market.stock.enums.MarketType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * KIS 종목 정보 파일 파싱 결과 내부 DTO.
 * Service에서 Stock 엔티티로 변환할 때 사용.
 */
@Getter
@AllArgsConstructor
public class StockInfoDto {
    private final MarketType marketType; // KOSPI / KOSDAQ
    private final String symbol;         // KRX 종목 코드
    private final String name;           // 종목명
    private final String isin;           // ISIN
    private final String rawLine;        // 원본 라인 (디버깅용)
}
