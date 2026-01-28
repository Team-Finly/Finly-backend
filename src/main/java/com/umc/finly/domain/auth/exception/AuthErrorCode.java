package com.umc.finly.domain.auth.exception;
import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH409", "이미 가입된 이메일입니다."),

    // 400
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH400_1", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "AUTH400_2", "닉네임 형식이 올바르지 않습니다."),
    INVALID_TERM_REQUEST(HttpStatus.BAD_REQUEST, "AUTH400_3", "약관 동의 요청이 올바르지 않습니다."),
    INVALID_PERSONA_ANSWERS(HttpStatus.BAD_REQUEST, "AUTH400_4", "페르소나 답변이 올바르지 않습니다."),

    // 401
    INVALID_LOGIN_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401", "비밀번호가 일치하지 않습니다."),

    // 422
    REQUIRED_TERM_NOT_AGREED(HttpStatus.UNPROCESSABLE_ENTITY, "AUTH422", "필수 약관에 동의해야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
