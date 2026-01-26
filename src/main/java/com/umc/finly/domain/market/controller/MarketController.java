package com.umc.finly.domain.market.controller;

import com.umc.finly.domain.market.dto.MarketIndexResponse;
import com.umc.finly.domain.market.dto.MarketInsightResponse;

import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    /**
     * 코스피 / 코스닥 / 공포·탐욕 지수 조회
     * - Redis 캐시에 저장된 최신 데이터 반환
     */

    @GetMapping("/index")
    public ResponseEntity<ApiResponse<MarketIndexResponse>> getMarketIndex() {

    }

    /**
     * 실시간 시장 인사이트 조회
     * - Redis에 집계된 타 유저 데이터 기반
     */
    @GetMapping("/insight")
    public ResponseEntity<ApiResponse<MarketInsightResponse>> getMarketInsight() {

    }
}
