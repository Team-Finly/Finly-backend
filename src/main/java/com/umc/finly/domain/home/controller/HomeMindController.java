package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.response.HomeMindDetailResDTO;
import com.umc.finly.domain.home.dto.response.HomeMindResDTO;
import com.umc.finly.domain.home.service.HomeMindService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Tag(name = "Home", description = "금융 마음 지수 조회 API")
public class HomeMindController implements HomeMindApiSpecification{

    private final HomeMindService homeMindService;

    //금융 마음 지수 조회 API
    @GetMapping("/mind")
    public ApiResponse<HomeMindResDTO> getHomeMind(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeMindResDTO result =
                homeMindService.getHomeMind(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 금융 마음 지수 상세 조회 API
    @GetMapping("/mind/detail")
    public ApiResponse<HomeMindDetailResDTO> getHomeMindDetail(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        return ApiResponse.onSuccess(
                homeMindService.getHomeMindDetail(principal.getMemberId()),
                SuccessCode.OK
        );
    }

}
