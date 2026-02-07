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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;
    private final RecordFeedbackService feedbackService;

    @PostMapping
    public ApiResponse<RecordCreateResDTO> createRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody RecordCreateReqDTO request
    ) {
        RecordCreateResDTO result = recordService.createRecord(principal.getMemberId(), request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    @GetMapping("/today")
    public ApiResponse<TodayRecordResDTO> getTodayRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        TodayRecordResDTO result = recordService.getTodayRecords(principal.getMemberId(), date);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/search")
    public ApiResponse<RecordSearchResDTO> searchRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EmotionCode emotionCode
    ) {
        RecordSearchResDTO result = recordService.searchRecords(principal.getMemberId(), keyword, emotionCode);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/search/recent")
    public ApiResponse<RecentSearchResDTO> getRecentSearchKeywords(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        RecentSearchResDTO result = recordService.getRecentSearchKeywords(principal.getMemberId());
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{recordId}")
    public ApiResponse<RecordDetailResDTO> getRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordDetailResDTO result = recordService.getRecord(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @PatchMapping("/{recordId}")
    public ApiResponse<RecordUpdateResDTO> updateRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId,
            @Valid @RequestBody RecordUpdateReqDTO request
    ) {
        RecordUpdateResDTO result = recordService.updateRecord(principal.getMemberId(), recordId, request);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{recordId}/feedback")
    public ApiResponse<RecordFeedbackResDTO> getFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackResDTO result = feedbackService.getFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{recordId}/daily-report")
    public ApiResponse<DailyReportResDTO> getDailyReport(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        DailyReportResDTO result = recordService.getDailyReport(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @PostMapping("/{recordId}/feedback/regenerate")
    public ApiResponse<RecordFeedbackResDTO> regenerateFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackResDTO result = feedbackService.regenerateFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
