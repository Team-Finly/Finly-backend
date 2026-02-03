package com.umc.finly.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketInsightResponse {


    private String stockName; // 종목 이름
    private String message; // 캐러셀 문장
    private String dominantEmotion; // 감정
    private String buySellRatio; // BUY_DOMINANT / SELL_DOMINANT / BALANCED
    private String confidenceLevel; // // LOW / MEDIUM / HIGH
}
