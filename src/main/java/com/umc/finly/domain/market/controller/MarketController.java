package com.umc.finly.domain.market.controller;

import com.umc.finly.domain.market.dto.MarketIndexResDTO;
import com.umc.finly.domain.market.dto.MarketInsightResDTO;
import com.umc.finly.domain.market.service.MarketIndexService;
import com.umc.finly.domain.market.service.MarketInsightService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {
    private final MarketIndexService marketIndexService;
    private final MarketInsightService marketInsightService;

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
