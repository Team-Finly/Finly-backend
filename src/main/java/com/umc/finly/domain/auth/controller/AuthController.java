package com.umc.finly.domain.auth.controller;

import com.umc.finly.domain.auth.dto.CheckEmailRes;
import com.umc.finly.domain.auth.service.AuthService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<CheckEmailRes>> checkEmail(
            @RequestParam("email")
            @NotBlank(message = "유효한 이메일을 입력해 주세요.")
            @Email(message = "유효한 이메일을 입력해 주세요.")
            String email
    ) {
        boolean available = authService.isEmailAvailable(email);

        if(!available){
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        ApiResponse<CheckEmailRes> body = ApiResponse.onSuccess(
                CheckEmailRes.of(available),
                SuccessCode.OK
        );
        return ResponseEntity.status(SuccessCode.OK.getHttpStatus()).body(body);
    }
}
