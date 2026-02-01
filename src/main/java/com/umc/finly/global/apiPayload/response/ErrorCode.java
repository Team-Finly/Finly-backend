package com.umc.finly.global.apiPayload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // COMMON 4xx (클라이언트 오류)
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증되지 않은 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근이 금지되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청하신 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않은 HTTP 메서드입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "COMMON406", "받아들일 수 없는 요청입니다."),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "COMMON408", "요청 시간이 초과되었습니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON409", "서버 상태와 충돌하는 요청입니다."),

    // AUTH 4xx
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH409", "이미 가입된 이메일입니다."),

    // RECORD 4xx
    RECORD_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RECORD400", "필수값 또는 형식 오류입니다."),
    RECORD_DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "RECORD409", "중복 제출된 요청입니다."),

    // MARKET 4xx
    MARKET_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404", "종목을 찾을 수 없습니다."),

    // COMMON 5xx (서버 오류)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "COMMON502", "잘못된 게이트웨이 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서비스를 사용할 수 없습니다."),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "COMMON504", "게이트웨이 요청 시간이 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
