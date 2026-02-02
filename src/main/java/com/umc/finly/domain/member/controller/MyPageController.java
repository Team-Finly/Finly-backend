package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.member.dto.response.MyPagePersonaRes;
import com.umc.finly.domain.member.service.MyPageService;
import com.umc.finly.global.apiPayload.exception.CustomException;
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
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/persona")
    public ApiResponse<MyPagePersonaRes> getMyPersona(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        if(principal == null){
            throw new CustomException(AuthErrorCode.UNATHORIZED);
        }
        return ApiResponse.onSuccess(
                myPageService.getMyPersona(principal.getMemberId()),
                SuccessCode.OK
        );
    }
}
