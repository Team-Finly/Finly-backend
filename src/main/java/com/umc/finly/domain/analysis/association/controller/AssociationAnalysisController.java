package com.umc.finly.domain.analysis.association.controller;

import com.umc.finly.domain.analysis.association.dto.*;
import com.umc.finly.domain.analysis.association.service.*;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AssociationAnalysisController implements AssociationAnalysisApiSpecification{

    private final AnalysisEntryService analysisEntryService;
    private final DailyChartService dailyChartService;
    private final AnalysisStockService analysisStockService;
    private final FearIndexService fearIndexService;
    private final ConvictionScoreService convictionScoreService;

    // 사용자 통계 진입 상태 조회 API
    @GetMapping("/entry")
    public ApiResponse<AnalysisEntryResDTO> getEntryStatus(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        AnalysisEntryResDTO result = analysisEntryService.getEntryStatus(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }


    // 사용자가 기록한 종목 목록 조회 API
    @GetMapping("/record/stocks")
    public ApiResponse<List<AnalysisStockResDTO>> getRecordStocks(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        return ApiResponse.onSuccess(
                analysisStockService.getRecordedStocks(principal.getMemberId()),
                SuccessCode.OK
        );
    }


    // 일별 주가 감정 그래프 조회 API
    @GetMapping("/stocks/{symbol}/charts/daily")
    public ApiResponse<DailyChartResDTO> getDailyChart(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    ) {
        DailyChartResDTO result = dailyChartService.getDailyChart(principal.getMemberId(), symbol);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 시간별 그래프 조회 API
    //@GetMapping("/stocks/{symbol}/charts/hourly")

    // 하락장 공포지수 조회 API
    @GetMapping("/fear-index")
    public ApiResponse<FearIndexResDTO> getFearIndex(
            @AuthenticationPrincipal AuthPrincipal principal) {

        FearIndexResDTO result = fearIndexService.getFearIndex(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 매수 확신도 조회 API
    @GetMapping("/conviction-score")
    public ApiResponse<ConvictionScoreResDTO> getConvictionScore(
            @AuthenticationPrincipal AuthPrincipal principal) {

        ConvictionScoreResDTO result = convictionScoreService.getConvictionScore(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // AI 패턴분석 조회 API
    //@GetMapping("/association/pattern")
}
