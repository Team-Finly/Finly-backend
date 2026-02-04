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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 · 회원가입 · 로그인")
public class AuthController {
    // 인증 관련 비즈니스 로직 담당 서비스

    private final AuthService authService;

    private final CookieUtil cookieUtil;

    /**
     * 이메일 중복 확인
     */
    @Operation(
            summary = "이메일 중복 확인",
            description = """
                    회원가입 전에 이메일 중복 여부를 확인합니다.
                    
                    - 사용 가능한 이메일이면 성공 응답을 반환합니다.
                    - 이미 가입된 이메일인 경우 EMAIL_ALREADY_EXISTS 에러를 반환합니다.
                    """
    )
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

    /**
     * 회원가입
     */
    @Operation(
            summary = "회원가입",
            description = """
                    신규 사용자를 회원으로 등록합니다.
                    
                    - 이메일, 비밀번호, 닉네임 정보를 기반으로 회원을 생성합니다.
                    - 필수 약관 동의 여부를 검증합니다.
                    - 회원가입 성공 시 회원 기본 정보와 personaId를 반환합니다.
                    """
    )
    @PostMapping("/signup")
    public ApiResponse<AuthSignUpResDTO> signup(@RequestBody @Valid AuthSignUpReqDTO request){
        AuthSignUpResDTO result = authService.signup(request);
        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    /**
     * 로그인
     */
    @Operation(
            summary = "로그인",
            description = """
                    이메일과 비밀번호로 로그인합니다.
                    
                    - 로그인 성공 시 Access Token을 응답 바디로 반환합니다.
                    - Refresh Token은 HttpOnly Cookie로 설정됩니다.
                    """
    )
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

    /**
     * 토큰 재발급
     */
    @Operation(
            summary = "Access Token 재발급",
            description = """
                    Refresh Token을 사용해 Access Token을 재발급합니다.
                    
                    - Refresh Token은 HttpOnly Cookie에서 자동으로 전달됩니다.
                    - Refresh Token이 유효하면 새로운 Access Token을 반환합니다.
                    - Refresh Token이 만료되었거나 유효하지 않으면 재로그인이 필요합니다.
                    """
    )
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
