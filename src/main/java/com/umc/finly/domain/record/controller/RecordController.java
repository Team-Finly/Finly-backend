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

// 투자 기록(Record) 관련 API 컨트롤러
// 기록 CRUD, 검색, AI 피드백, 데일리 리포트 등을 담당함
@RestController
@RequiredArgsConstructor
@Tag(name = "기록 record", description = "로그인한 사용자의 기록 관련 API")
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService; // 기록 비즈니스 로직 처리
    private final RecordFeedbackService feedbackService; // AI 피드백 관련 로직 처리

    // 새로운 투자 기록 생성
    // 생성 후 AI 피드백이 비동기로 자동 요청됨
    @Operation(summary = "기록 생성 API", description = "새로운 기록을 생성하고 AI 피드백을 비동기로 요청")
    @PostMapping
    public ApiResponse<RecordCreateResDTO> createRecord(
            @AuthenticationPrincipal AuthPrincipal principal, // JWT에서 추출한 사용자 정보
            @Valid @RequestBody RecordCreateReqDTO request // 요청 본문 유효성 검사 포함
    ) {
        RecordCreateResDTO result = recordService.createRecord(principal.getMemberId(), request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    // 특정 날짜의 기록 목록 + 프리즘 피드백 조회
    // 날짜는 쿼리 파라미터로 ISO 형식(yyyy-MM-dd)으로 받음
    @Operation(summary = "해당 날짜 마음 조각 조회 API", description = "특정 날짜의 기록 목록과 프리즘 피드백을 조회")
    @GetMapping("/today")
    public ApiResponse<TodayRecordResDTO> getTodayRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        TodayRecordResDTO result = recordService.getTodayRecords(principal.getMemberId(), date);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 키워드 또는 감정 코드로 기록 검색
    // 둘 다 선택적 파라미터이며, 검색 시 키워드는 검색 기록에 저장됨
    @Operation(summary = "기록 검색 API", description = "키워드 또는 감정 코드로 기록을 검색")
    @GetMapping("/search")
    public ApiResponse<RecordSearchResDTO> searchRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) String keyword, // 종목명 또는 메모 검색용
            @RequestParam(required = false) EmotionCode emotionCode // 감정 필터링용
    ) {
        RecordSearchResDTO result = recordService.searchRecords(principal.getMemberId(), keyword, emotionCode);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 최근 검색한 키워드 목록 조회 (최대 3개)
    @Operation(summary = "최근 검색 기록 API", description = "최근 검색한 키워드 목록을 조회 (최대 3개)")
    @GetMapping("/search/recent")
    public ApiResponse<RecentSearchResDTO> getRecentSearchKeywords(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        RecentSearchResDTO result = recordService.getRecentSearchKeywords(principal.getMemberId());
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 단일 기록 상세 조회
    // 본인 기록만 조회 가능 (권한 검사 포함)
    @Operation(summary = "기록 상세 조회 API", description = "특정 기록의 상세 정보를 조회")
    @GetMapping("/{recordId}")
    public ApiResponse<RecordDetailResDTO> getRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordDetailResDTO result = recordService.getRecord(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 기록 부분 수정 (PATCH)
    // null이 아닌 필드만 업데이트됨
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

    // AI 피드백 조회
    // 피드백 상태: PENDING(대기) -> GENERATING(생성중) -> COMPLETED(완료) / FAILED(실패)
    @Operation(summary = "AI피드백 결과 조회 API", description = "특정 기록에 대한 AI 피드백을 조회")
    @GetMapping("/{recordId}/feedback")
    public ApiResponse<RecordFeedbackResDTO> getFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackResDTO result = feedbackService.getFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 데일리 리포트 조회
    // 메모를 AI로 요약한 결과를 반환함
    @Operation(summary = "데일리 리포트 AI요약 조회 API", description = "특정 기록의 메모를 AI로 요약한 데일리 리포트를 조회")
    @GetMapping("/{recordId}/daily-report")
    public ApiResponse<DailyReportResDTO> getDailyReport(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        DailyReportResDTO result = recordService.getDailyReport(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // AI 피드백 재생성 요청
    // 이미 생성 중인 경우 에러 반환 (중복 요청 방지)
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
