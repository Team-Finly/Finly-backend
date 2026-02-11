package com.umc.finly.domain.market.service;

import com.umc.finly.domain.market.dto.response.MarketInsightResDTO;

public interface MarketInsightService {

    // 실시간 시장 인사이트 조회
    MarketInsightResDTO getMarketInsight();
}