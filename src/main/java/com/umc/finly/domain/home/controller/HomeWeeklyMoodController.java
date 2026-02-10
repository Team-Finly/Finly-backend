package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.response.HomeWeeklyMoodResDTO;
import com.umc.finly.domain.home.service.HomeWeeklyMoodService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Tag(name = "Home", description = "위클리 무드 조회 API")
public class HomeWeeklyMoodController {

    private final HomeWeeklyMoodService homeWeeklyMoodService;

    @Operation(
            summary = "위클리 무드 조회",
            description = """
            로그인한 사용자의 이번 주(월~일) 기록을 기준으로 요일별 감정 요약 정보를 반환합니다.
            
            - 기록이 없는 요일은 hasRecord=false로 표시됩니다
            """
    )
    @GetMapping("/weekly")
    public ApiResponse<HomeWeeklyMoodResDTO> getWeeklyMood(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeWeeklyMoodResDTO result =
                homeWeeklyMoodService.getWeeklyMood(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
