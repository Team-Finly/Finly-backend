package com.umc.finly.domain.market.controller;

import com.umc.finly.domain.market.dto.response.MarketIndexResDTO;
import com.umc.finly.domain.market.dto.response.MarketInsightResDTO;
import com.umc.finly.domain.market.service.MarketIndexService;
import com.umc.finly.domain.market.service.MarketInsightService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
@Tag(name = "Market", description = "시장 지수 및 실시간 시장 인사이트 API")
public class MarketController implements MarketApiSpecification {
    private final MarketIndexService marketIndexService;
    private final MarketInsightService marketInsightService;

    // 시장 지수 조회 API
    @GetMapping("/index")
    public ApiResponse<MarketIndexResDTO> getMarketIndex() {

        return ApiResponse.onSuccess(
                marketIndexService.getMarketIndex(),
                SuccessCode.OK
        );
    }

    // 실시간 인사이트 조회
    @GetMapping("/insight")
    public ApiResponse<MarketInsightResDTO> getInsightMarketIndex() {
        return ApiResponse.onSuccess(
                marketInsightService.getMarketInsight(),
                SuccessCode.OK
        );
    }

}
