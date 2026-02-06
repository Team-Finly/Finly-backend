package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum KoreaInvestErrorCode implements BaseCode {

    TOKEN_GENERATION_FAILED(HttpStatus.UNAUTHORIZED, "KI401", "한국투자증권 인증 토큰 발급에 실패했습니다."),
    API_CALL_ERROR(HttpStatus.BAD_GATEWAY, "KI502", "한국투자증권 API 호출 중 오류가 발생했습니다."),
    INVALID_SYMBOL(HttpStatus.BAD_REQUEST, "KI400", "종목 코드 형식이 올바르지 않습니다. 종목 코드는 숫자 6자리 형식이어야 합니다." ),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "KI404", "존재하지 않는 종목입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
