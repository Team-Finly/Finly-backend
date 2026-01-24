package com.umc.finly.domain.record.controller;

import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.service.RecordService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<ApiResponse<RecordCreateRes>> createRecord(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RecordCreateReq request
    ) {
        RecordCreateRes result = recordService.createRecord(userId, request);

        ApiResponse<RecordCreateRes> body = ApiResponse.onSuccess(result, SuccessCode.CREATED);
        return ResponseEntity.status(SuccessCode.CREATED.getHttpStatus()).body(body);
    }
}
