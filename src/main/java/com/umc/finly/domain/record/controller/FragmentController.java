package com.umc.finly.domain.record.controller;

import com.umc.finly.domain.record.dto.res.FragmentCalendarResDTO;
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

// 조각 모음함(Fragment) 관련 API 컨트롤러
// 감정별 기록 통계 및 리스트 조회를 담당함
@RequiredArgsConstructor
@RestController
@Tag(name = "Fragments", description = "조각 모음함 관련 API")
@RequestMapping("/api/records/fragments")
public class FragmentController {

    private final FragmentService fragmentService;

    // 조각 모음함 요약 조회
    // 전체 기록 수, 가장 많은 감정(dominantType), 감정별 개수/비율 반환
    @Operation(summary = "조각 모음함 요약 조회 API", description = "로그인한 본인(member)의 조각 모음함 요약을 조회")
    @GetMapping("/summary")
    public ApiResponse<FragmentSummaryResDTO> getFragmentSummary(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        // principal null 체크 (인증 실패 시)
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        FragmentSummaryResDTO result = fragmentService.getFragmentSummary(principal.getMemberId());
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 조각 모음함 리스트 조회
    // 감정(boxType)과 기간(periodKey)으로 필터링 가능
    @Operation(summary = "조각 모음함 리스트 조회 API", description = "각 감정의 조각 모음함 전체 조회")
    @GetMapping
    public ApiResponse<FragmentListResDTO> getFragmentList(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) EmotionCode boxType, // null이면 전체 감정 조회
            @RequestParam(defaultValue = "ALL") FragmentPeriodKey periodKey // ALL, ONE_MONTH, THREE_MONTHS 등
    ) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        FragmentListResDTO result = fragmentService.getFragmentList(
                principal.getMemberId(), boxType, periodKey
        );

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 기록 홈 캘린더용 날짜별 조각 집계 조회
    // yearMonth(yyyy-MM) 기준으로 해당 월 범위 내 날짜별/감정별 count를 반환
    @Operation(summary = "캘린더용 날짜별 조각 조회 API", description = "기록 홈 월 캘린더에서 날짜 별 조각 여부/개수를 표시하기 위한 API")
    @GetMapping("/calendar")
    public ApiResponse<FragmentCalendarResDTO> getFragmentCalendar(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) String yearMonth
    ) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        FragmentCalendarResDTO result = fragmentService.getFragmentCalendar(principal.getMemberId(), yearMonth);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

}
