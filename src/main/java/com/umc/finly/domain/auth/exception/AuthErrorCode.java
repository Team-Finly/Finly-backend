package com.umc.finly.domain.auth.exception;
import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH409", "이미 가입된 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
