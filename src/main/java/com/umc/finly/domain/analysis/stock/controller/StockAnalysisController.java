package com.umc.finly.domain.analysis.stock.controller;

import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryRes;
import com.umc.finly.domain.analysis.stock.service.StockAnalysisService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis/stocks")
public class StockAnalysisController {
    private final StockAnalysisService stockAnalysisService;

    @GetMapping("/{stockId}/summary")
    public ApiResponse<StockSummaryRes> getStockSummary(
            @PathVariable String stockId
    ) {
        StockSummaryRes result = stockAnalysisService.getStockSummary(stockId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
