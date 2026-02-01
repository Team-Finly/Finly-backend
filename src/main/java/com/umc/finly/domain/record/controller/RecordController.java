package com.umc.finly.domain.record.controller;

import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.service.RecordService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ApiResponse<RecordCreateRes> createRecord(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody RecordCreateReq request
    ) {
        RecordCreateRes result = recordService.createRecord(principal.getMemberId(), request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }
}
