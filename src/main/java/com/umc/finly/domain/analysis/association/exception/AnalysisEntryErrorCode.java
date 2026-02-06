package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisEntryErrorCode implements BaseCode {

    TOP_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS_ENTRY404", "사용자가 가장 많이 기록한 종목을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
