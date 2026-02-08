package com.umc.finly.domain.record.controller;

import com.umc.finly.domain.record.dto.res.FragmentSummaryResDTO;
import com.umc.finly.domain.record.dto.res.FragmentListResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;
import com.umc.finly.domain.record.service.FragmentService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Fragments", description = "조각 모음함 관련 API")
@RequestMapping("/api/records/fragments")
public class FragmentController {

    private final FragmentService fragmentService;

    @Operation(summary = "조각 모음함 요약 조회 API", description = "로그인한 본인(member)의 조각 모음함 요약을 조회")
    @GetMapping("/summary")
    public ApiResponse<FragmentSummaryResDTO> getFragmentSummary(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        if(principal == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        FragmentSummaryResDTO result = fragmentService.getFragmentSummary(principal.getMemberId());
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "조각 모음함 리스트 조회 API", description = "각 감정의 조각 모음함 전체 조회")
    @GetMapping
    public ApiResponse<FragmentListResDTO> getFragmentList(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) EmotionCode boxType,
            @RequestParam(defaultValue = "ALL") FragmentPeriodKey periodKey
    ) {
        if(principal == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        FragmentListResDTO result = fragmentService.getFragmentList(
                principal.getMemberId(), boxType, periodKey
        );

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
