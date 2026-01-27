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

    private String message;
    private String dominantEmotion;
    private String buySellRatio;
    private String confidenceLevel;
}
