package com.umc.finly.domain.analysis.stock.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisErrorCode implements BaseCode {

    // 주식 데이터 탭
    // 4xx
    STOCK_CURRENT_PRICE_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS_STOCK404", "존재하지 않는 종목입니다."),

    // 5xx
    STOCK_CURRENT_PRICE_API_FAILED(HttpStatus.BAD_GATEWAY, "ANALYSIS_STOCK502_1", "주식 현재가 조회 중 외부 API 오류가 발생했습니다."),
    STOCK_CURRENT_PRICE_RESPONSE_INVALID(HttpStatus.BAD_GATEWAY, "ANALYSIS_STOCK502_2", "주식 현재가 응답 형식이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
