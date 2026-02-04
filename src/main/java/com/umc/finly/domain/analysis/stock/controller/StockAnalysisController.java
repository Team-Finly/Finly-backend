package com.umc.finly.domain.analysis.stock.controller;

import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryRes;
import com.umc.finly.domain.analysis.stock.service.StockAnalysisService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis/stocks")
public class StockAnalysisController {
    private final StockAnalysisService stockAnalysisService;

    @Operation(
            summary = "선택 종목 분석 조회",
            description = """
            선택한 종목에 대해 로그인한 사용자의 매수 기록을 기반으로 종목 분석 조회 정보를 반환합니다.
            
            계산 기준
            - 매수(BUY) 기록만 집계합니다.
            - 평균 매수 가액 = Σ(unit_price * quantity) / Σ(quantity)
            - 현재가 = 실시간 현재가(5초 단위 캐싱)
            - 누적 매수 횟수 = 매수 기록 개수
            - 최대 보유 기간 = O(n) 스캔으로 계산
            - 날짜 단위 기록만 제공되므로 동일 날짜의 BUY/SELL 순서는 구분할 수 없습니다.
              이에 따라 최대 보유 기간은 일 단위 기준으로 계산하며, 동일 날짜의 기록은 BUY를 SELL보다 우선 처리하도록 정렬하여 계산합니다.
            
            유의사항
            - 매수 기록이 없는 경우 0으로 반환됩니다.
            - 현재가는 외부 API 기준 값입니다.
            """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "존재하지 않는 종목"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "서버 내부 오류"
            )
    })
    @GetMapping("/{symbol}/summary")
    public ApiResponse<StockSummaryRes> getStockSummary(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    ) {
        StockSummaryRes result = stockAnalysisService.getStockSummary(principal.getMemberId(), symbol);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
