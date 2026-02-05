package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.HomeRecordsRes;
import com.umc.finly.domain.home.service.HomeRecordService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeRecordController {

    private final HomeRecordService homeRecordService;

    @GetMapping("/records")
    public ApiResponse<HomeRecordsRes> getRecentMyRecords(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeRecordsRes result = homeRecordService.getRecentMyRecords(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
