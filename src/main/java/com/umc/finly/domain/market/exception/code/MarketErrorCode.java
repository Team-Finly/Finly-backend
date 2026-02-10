package com.umc.finly.domain.market.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MarketErrorCode implements BaseCode {

    // 시장 지표
    //x4xx
    MARKET_INDEX_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_1", "시장 지표 데이터가 존재하지 않습니다."),
    MARKET_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404_1", "종목 데이터가 존재하지 않습니다."),
    // 5xx
    MARKET_INDEX_API_FAILED(HttpStatus.BAD_GATEWAY, "MARKET502", "시장 지표 조회 API 호출에 실패했습니다."),
    MARKET_INDEX_RESPONSE_INVALID(HttpStatus.BAD_GATEWAY, "MARKET503", "시장 지표 응답 형식이 올바르지 않습니다."),

    // 인사이트
    MARKET_INSIGHT_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKET404", "실시간 인사이트 데이터를 불러올 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
