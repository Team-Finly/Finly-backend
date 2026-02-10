package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.response.HomeRecordsResDTO;
import com.umc.finly.domain.home.service.HomeRecordService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Tag(name = "Home", description = "최근 나의 기록 조회 API")
public class HomeRecordController implements HomeRecordApiSpecification{

    private final HomeRecordService homeRecordService;

    // 최근 나의 기록 조회 API
    @GetMapping("/records")
    public ApiResponse<HomeRecordsResDTO> getRecentMyRecords(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeRecordsResDTO result = homeRecordService.getRecentMyRecords(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
