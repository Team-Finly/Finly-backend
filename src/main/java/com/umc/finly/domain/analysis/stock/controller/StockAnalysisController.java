package com.umc.finly.domain.analysis.stock.controller;

import com.umc.finly.domain.analysis.stock.dto.response.PriceDistributionResDTO;
import com.umc.finly.domain.analysis.stock.dto.response.RecentDecisionResDTO;
import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryResDTO;
import com.umc.finly.domain.analysis.stock.service.StockAnalysisService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "StockAnalysis",
        description = "통계 - 주식 데이터 관련 API"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis/stocks")
public class StockAnalysisController implements StockAnalysisApiSpecification{
    private final StockAnalysisService stockAnalysisService;

    // 선택 종목 분석 조회 API
    @GetMapping("/{symbol}/summary")
    public ApiResponse<StockSummaryResDTO> getStockSummary(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    ) {
        StockSummaryResDTO result = stockAnalysisService.getStockSummary(principal.getMemberId(), symbol);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 가격대별 기록 분포 조회 API
    @GetMapping("/{symbol}/price-distribution")
    public ApiResponse<PriceDistributionResDTO> getPriceDistribution(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    ) {
        PriceDistributionResDTO result =
                stockAnalysisService.getPriceDistribution(principal.getMemberId(), symbol);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 최근 판단 결과 조회 API
    @GetMapping("/{symbol}/recent")
    public ApiResponse<List<RecentDecisionResDTO>> getRecentDecisions(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol,
            @RequestParam(defaultValue = "3") int limit
    ) {
        List<RecentDecisionResDTO> result = stockAnalysisService.getRecentDecisions(principal.getMemberId(), symbol, limit);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
