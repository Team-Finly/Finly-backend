package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.member.dto.request.UpdateNicknameReq;
import com.umc.finly.domain.member.dto.response.MyPageMeRes;
import com.umc.finly.domain.member.dto.response.MyPagePersonaRes;
import com.umc.finly.domain.member.dto.response.UpdateNicknameRes;
import com.umc.finly.domain.member.service.MyPageService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    // 내 페르소나 조회
    @GetMapping("/persona")
    public ApiResponse<MyPagePersonaRes> getMyPersona(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        if(principal == null){
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }
        return ApiResponse.onSuccess(
                myPageService.getMyPersona(principal.getMemberId()),
                SuccessCode.OK
        );
    }

    // 내 프로필 조회
    @GetMapping("/me")
    public ApiResponse<MyPageMeRes> getMyInfo(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        if (principal == null){
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.onSuccess(
                myPageService.getMyInfo(principal.getMemberId()),
                SuccessCode.OK
        );
    }

    // 내 닉네임 변경
    @PostMapping("/me/nickname")
    public ApiResponse<UpdateNicknameRes> updateNickname(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody @Valid UpdateNicknameReq request
            ){

        if (principal == null){
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.onSuccess(
                myPageService.updateMyNickname(
                        principal.getMemberId(),
                        request.getNickname()
                ),
                SuccessCode.OK
        );
    }
}
