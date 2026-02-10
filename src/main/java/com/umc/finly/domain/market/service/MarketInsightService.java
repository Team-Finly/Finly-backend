package com.umc.finly.domain.market.service;

import com.umc.finly.domain.market.converter.MarketInsightConverter;
import com.umc.finly.domain.market.dto.response.MarketInsightResDTO;
import com.umc.finly.domain.market.repository.MarketInsightRepository;
import com.umc.finly.domain.market.repository.projection.StockEmotionBuyAggregation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.umc.finly.domain.record.enums.EmotionCode;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public interface MarketInsightService {

    // 실시간 시장 인사이트 조회
    MarketInsightResDTO getMarketInsight();
}