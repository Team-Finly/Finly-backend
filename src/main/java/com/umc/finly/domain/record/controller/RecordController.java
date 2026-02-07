package com.umc.finly.domain.record.controller;

import com.umc.finly.domain.record.dto.req.RecordCreateReqDTO;
import com.umc.finly.domain.record.dto.req.RecordUpdateReqDTO;
import com.umc.finly.domain.record.dto.res.DailyReportResDTO;
import com.umc.finly.domain.record.dto.res.RecentSearchResDTO;
import com.umc.finly.domain.record.dto.res.RecordCreateResDTO;
import com.umc.finly.domain.record.dto.res.RecordDetailResDTO;
import com.umc.finly.domain.record.dto.res.RecordFeedbackResDTO;
import com.umc.finly.domain.record.dto.res.RecordSearchResDTO;
import com.umc.finly.domain.record.dto.res.RecordUpdateResDTO;
import com.umc.finly.domain.record.dto.res.TodayRecordResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.service.RecordFeedbackService;
import com.umc.finly.domain.record.service.RecordService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "기록 record", description = "로그인한 사용자의 기록 관련 API")
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;
    private final RecordFeedbackService feedbackService;

    @Operation(summary = "기록 생성 API", description = "새로운 기록을 생성하고 AI 피드백을 비동기로 요청")
    @PostMapping
    public ApiResponse<RecordCreateResDTO> createRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody RecordCreateReqDTO request
    ) {
        RecordCreateResDTO result = recordService.createRecord(principal.getMemberId(), request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    @Operation(summary = "해당 날짜 마음 조각 조회 API", description = "특정 날짜의 기록 목록과 프리즘 피드백을 조회")
    @GetMapping("/today")
    public ApiResponse<TodayRecordResDTO> getTodayRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        TodayRecordResDTO result = recordService.getTodayRecords(principal.getMemberId(), date);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "기록 검색 API", description = "키워드 또는 감정 코드로 기록을 검색")
    @GetMapping("/search")
    public ApiResponse<RecordSearchResDTO> searchRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EmotionCode emotionCode
    ) {
        RecordSearchResDTO result = recordService.searchRecords(principal.getMemberId(), keyword, emotionCode);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "최근 검색 기록 API", description = "최근 검색한 키워드 목록을 조회 (최대 3개)")
    @GetMapping("/search/recent")
    public ApiResponse<RecentSearchResDTO> getRecentSearchKeywords(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        RecentSearchResDTO result = recordService.getRecentSearchKeywords(principal.getMemberId());
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "기록 상세 조회 API", description = "특정 기록의 상세 정보를 조회")
    @GetMapping("/{recordId}")
    public ApiResponse<RecordDetailResDTO> getRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordDetailResDTO result = recordService.getRecord(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "기록 수정(제출) API", description = "특정 기록의 정보를 부분 수정")
    @PatchMapping("/{recordId}")
    public ApiResponse<RecordUpdateResDTO> updateRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId,
            @Valid @RequestBody RecordUpdateReqDTO request
    ) {
        RecordUpdateResDTO result = recordService.updateRecord(principal.getMemberId(), recordId, request);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "AI피드백 결과 조회 API", description = "특정 기록에 대한 AI 피드백을 조회")
    @GetMapping("/{recordId}/feedback")
    public ApiResponse<RecordFeedbackResDTO> getFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackResDTO result = feedbackService.getFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "데일리 리포트 AI요약 조회 API", description = "특정 기록의 메모를 AI로 요약한 데일리 리포트를 조회")
    @GetMapping("/{recordId}/daily-report")
    public ApiResponse<DailyReportResDTO> getDailyReport(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        DailyReportResDTO result = recordService.getDailyReport(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "AI피드백 재생성 요청 API", description = "특정 기록의 AI 피드백을 새로 생성")
    @PostMapping("/{recordId}/feedback/regenerate")
    public ApiResponse<RecordFeedbackResDTO> regenerateFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackResDTO result = feedbackService.regenerateFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
