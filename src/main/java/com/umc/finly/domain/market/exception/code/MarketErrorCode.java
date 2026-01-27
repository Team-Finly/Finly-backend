package com.umc.finly.domain.market.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MarketErrorCode implements BaseCode {

    // 시장 지표
    MARKET_INDEX_NOT_FOUND(
            ErrorCode.NOT_FOUND,
            "MARKET404",
            "시장 지표 데이터가 존재하지 않습니다."
    ),

    // 인사이트
    MARKET_INSIGHT_NOT_FOUND(
            ErrorCode.NOT_FOUND,
            "MARKET404",
            "실시간 인사이트 데이터를 불러올 수 없습니다."
    );

    private final ErrorCode errorCode; // global ErrorCode
    private final String code;         // MARKETxxx
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
