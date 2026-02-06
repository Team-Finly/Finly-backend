package com.umc.finly.domain.analysis.emotion.controller;

import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.service.EmotionAnalysisService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Analysis - emotion", description = "통계 감정 분석 API")
@RequestMapping("/api/analysis/stocks")
public class EmotionAnalysisController {

    private final EmotionAnalysisService emotionAnalysisService;

    @Operation(summary = "감정 분포 그래프 API", description = "각 종목의 조각 감정 분포 통계")
    @GetMapping("/{symbol}/emotion-distribution")
    public ApiResponse<EmotionDistributionResDTO> getEmotionDistribution(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String symbol
    ) {
        if(principal == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        EmotionDistributionResDTO result =
                emotionAnalysisService.getEmotionDistribution(principal.getMemberId(), symbol);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}