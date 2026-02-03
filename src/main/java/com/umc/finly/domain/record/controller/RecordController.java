package com.umc.finly.domain.record.controller;

import com.umc.finly.domain.record.dto.DailyReportRes;
import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.dto.RecordDetailRes;
import com.umc.finly.domain.record.dto.RecordFeedbackRes;
import com.umc.finly.domain.record.dto.RecordUpdateReq;
import com.umc.finly.domain.record.dto.RecordUpdateRes;
import com.umc.finly.domain.record.dto.TodayRecordRes;
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
    public ApiResponse<RecordCreateRes> createRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody RecordCreateReq request
    ) {
        RecordCreateRes result = recordService.createRecord(principal.getMemberId(), request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    @GetMapping("/today")
    public ApiResponse<TodayRecordRes> getTodayRecords(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        TodayRecordRes result = recordService.getTodayRecords(principal.getMemberId(), date);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{recordId}")
    public ApiResponse<RecordDetailRes> getRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordDetailRes result = recordService.getRecord(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @PatchMapping("/{recordId}")
    public ApiResponse<RecordUpdateRes> updateRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId,
            @Valid @RequestBody RecordUpdateReq request
    ) {
        RecordUpdateRes result = recordService.updateRecord(principal.getMemberId(), recordId, request);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{recordId}/feedback")
    public ApiResponse<RecordFeedbackRes> getFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackRes result = feedbackService.getFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{recordId}/daily-report")
    public ApiResponse<DailyReportRes> getDailyReport(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        DailyReportRes result = recordService.getDailyReport(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @PostMapping("/{recordId}/feedback/regenerate")
    public ApiResponse<RecordFeedbackRes> regenerateFeedback(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long recordId
    ) {
        RecordFeedbackRes result = feedbackService.regenerateFeedback(principal.getMemberId(), recordId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
