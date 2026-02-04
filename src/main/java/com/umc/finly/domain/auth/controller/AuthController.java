package com.umc.finly.domain.auth.controller;

import com.umc.finly.domain.auth.dto.req.AuthLoginReqDTO;
import com.umc.finly.domain.auth.dto.req.AuthSignUpReqDTO;
import com.umc.finly.domain.auth.dto.res.AuthLoginResDTO;
import com.umc.finly.domain.auth.dto.res.AuthReissueResDTO;
import com.umc.finly.domain.auth.dto.res.AuthSignUpResDTO;
import com.umc.finly.domain.auth.dto.res.CheckEmailResDTO;
import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.auth.service.AuthService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// 인증 관련 비즈니스 로직 담당 서비스
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final CookieUtil cookieUtil;

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ApiResponse<CheckEmailResDTO> checkEmail(
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

        return ApiResponse.onSuccess(CheckEmailResDTO.of(true),
                SuccessCode.OK
        );
    }

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<AuthSignUpResDTO> signup(@RequestBody @Valid AuthSignUpReqDTO request){
        AuthSignUpResDTO result = authService.signup(request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<AuthLoginResDTO> login(
            @RequestBody @Valid AuthLoginReqDTO request,
            HttpServletResponse response
            ){
        AuthService.LoginTokens tokens = authService.login(request);

        cookieUtil.addRefreshTokenCookie(
                response,
                tokens.refreshToken(),
                tokens.refreshMaxAgeSeconds()
        );

        return ApiResponse.onSuccess(tokens.body(), SuccessCode.OK);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ApiResponse<AuthReissueResDTO> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ){
        AuthService.ReissueTokens tokens = authService.reissue(refreshToken);
        cookieUtil.addRefreshTokenCookie(response, tokens.refreshToken(), tokens.refreshMaxAgeSeconds());

        return ApiResponse.onSuccess(
                AuthReissueResDTO.of(tokens.accessToken()),
                SuccessCode.OK
        );
    }
}
