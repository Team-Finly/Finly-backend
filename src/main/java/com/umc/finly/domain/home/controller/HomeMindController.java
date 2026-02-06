package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.res.HomeMindRes;
import com.umc.finly.domain.home.service.HomeMindService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeMindController {

    private final HomeMindService homeMindService;

    @GetMapping("/mind")
    public ApiResponse<HomeMindRes> getHomeMind(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeMindRes result =
                homeMindService.getHomeMind(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
