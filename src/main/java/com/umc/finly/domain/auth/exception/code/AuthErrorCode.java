package com.umc.finly.domain.auth.exception.code;
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
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH400_5", "비밀번호 확인이 일치하지 않습니다."),

    // 401
    INVALID_LOGIN_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401_2", "인증이 필요합니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_3", "액세스 토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_4", "유효하지 않은 액세스 토큰입니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH401_5", "refreshToken 쿠키가 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_6", "refreshToken이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_7", "유효하지 않은 refreshToken입니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH401_8", "서버에 저장된 refreshToken과 일치하지 않습니다."),

    //403
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH403", "접근 권한이 없습니다."),

    // 404
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH404", "해당 약관이 없습니다."),

    // 422
    REQUIRED_TERM_NOT_AGREED(HttpStatus.UNPROCESSABLE_ENTITY, "AUTH422", "필수 약관에 동의해야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
