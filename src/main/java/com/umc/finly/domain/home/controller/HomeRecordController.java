package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.res.HomeRecordsResDTO;
import com.umc.finly.domain.home.service.HomeRecordService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class HomeRecordController {

    private final HomeRecordService homeRecordService;
    @Operation(
            summary = "최근 나의 기록 조회",
            description = """
        홈 화면에서 로그인한 사용자의 최근 기록 목록을 조회합니다.

        조회 기준
        - 검색 조건 없이 전체 기록을 기준으로 조회합니다.
        - 오늘 기준 최근 3일 이내 기록만 필터링합니다.
        - 기록은 최신순(recordedAt DESC)으로 정렬됩니다.
        - 최대 5개의 기록만 반환됩니다.

        처리 방식
        - 기존 기록 검색 로직을 재사용하여 조회합니다.
        - Home 도메인에서 기간 및 개수 정책을 적용합니다.
        - 기록이 없는 경우 빈 배열을 반환합니다.

        유의사항
        - 인증되지 않은 사용자는 조회할 수 없습니다.
        - 기록 날짜(recordDate)는 일 단위 기준입니다.
        - 최신순 정렬 기준은 recordedAt(생성 시각)입니다.
        """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "최근 나의 기록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })

    @GetMapping("/records")
    public ApiResponse<HomeRecordsResDTO> getRecentMyRecords(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeRecordsResDTO result = homeRecordService.getRecentMyRecords(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
