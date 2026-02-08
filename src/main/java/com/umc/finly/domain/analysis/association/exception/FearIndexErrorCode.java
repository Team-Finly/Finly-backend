package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FearIndexErrorCode implements BaseCode {

    FEAR_INDEX_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS404", "하락장 공포지수 분석 결과가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
