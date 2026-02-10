package com.umc.finly.domain.analysis.stock.controller;

import com.umc.finly.domain.analysis.stock.dto.response.PriceDistributionResDTO;
import com.umc.finly.domain.analysis.stock.dto.response.RecentDecisionResDTO;
import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryResDTO;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface StockAnalysisApiSpecification {

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
    public ApiResponse<StockSummaryResDTO> getStockSummary(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    );

    @Operation(
            summary = "가격대별 기록 분포 조회",
            description = """
            선택한 종목에 대해 로그인한 사용자의 매수 기록을 기반으로 평균 매수가 대비 가격대별 기록 분포 조회 정보를 반환합니다.
            
            계산 기준
            - 평균 매수가 기준 ±5% 구간을 MID로 설정
            - LOW / MID / HIGH 3개 구간으로 분류
            - 매수(BUY) 기록만 집계
            - 비율 합이 100이 되도록 보정
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
    public ApiResponse<PriceDistributionResDTO> getPriceDistribution(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    );

    @Operation(
            summary = "최근 판단 결과 조회",
            description = """
            사용자가 기록한 매도 완료 기록을 기준으로 최근 판단 결과 조회 정보를 반환합니다.
            
            계산 기준
            - 날짜 단위 기록만 제공되므로 동일 날짜의 BUY/SELL 순서는 구분할 수 없습니다.
              이에 따라 동일 날짜의 매도는 해당 날짜 이전의 매수 판단을 기준으로 계산됩니다.
            - 수익률(%) = (매도금액 - 매수금액) / 매수금액 × 100이며
              여기서 매수금액은 이전 매도 이후부터 이번 매도 직전까지 발생한 매수 기록들의 합으로 정의됩니다.
            
            유의사항
            - 최근 판단 결과는 기본적으로 3개를 반환합니다. (limit 기본값 = 3)
            - 추후 설계 변경 및 확장성을 고려하여 limit 파라미터로 반환 개수를 조절할 수 있도록 해놓았습니다.
            - 실제 시세 기반 수익률이 아닙니다.
            - 사용자가 기록한 매수/매도 가격을 기준으로 계산됩니다.
            - 투자 판단 기록 분석을 위한 지표입니다.
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
    public ApiResponse<List<RecentDecisionResDTO>> getRecentDecisions(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol,
            @RequestParam(defaultValue = "3") int limit
    );
}
