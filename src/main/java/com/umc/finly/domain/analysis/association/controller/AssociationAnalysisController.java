package com.umc.finly.domain.analysis.association.controller;

import com.umc.finly.domain.analysis.association.dto.AnalysisEntryResDTO;
import com.umc.finly.domain.analysis.association.dto.DailyChartResDTO;
import com.umc.finly.domain.analysis.association.service.AnalysisEntryService;
import com.umc.finly.domain.analysis.association.service.DailyChartService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AssociationAnalysisController implements AssociationAnalysisApiSpecification{

    private final AnalysisEntryService analysisEntryService;
    private final DailyChartService dailyChartService;

    // 사용자 통계 진입 상태 조회 API
    @GetMapping("/entry")
    public ApiResponse<AnalysisEntryResDTO> getEntryStatus(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        AnalysisEntryResDTO result = analysisEntryService.getEntryStatus(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
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
}
