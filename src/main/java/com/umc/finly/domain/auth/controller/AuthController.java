package com.umc.finly.domain.auth.controller;

import com.umc.finly.domain.auth.dto.res.CheckEmailRes;
import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.auth.service.AuthService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 인증 관련 비즈니스 로직 담당 서비스
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ApiResponse<CheckEmailRes> checkEmail(
            @RequestParam("email")
            @NotBlank(message = "유효한 이메일을 입력해 주세요.")
            @Email(message = "유효한 이메일을 입력해 주세요.")
            String email
    ) {
        boolean available = authService.isEmailAvailable(email);

        // 이미 가입된 이메일인 경우 에러 응답
        if(!available){
            throw new CustomException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        return ApiResponse.onSuccess(CheckEmailRes.of(true),
                SuccessCode.OK
        );
    }
}
