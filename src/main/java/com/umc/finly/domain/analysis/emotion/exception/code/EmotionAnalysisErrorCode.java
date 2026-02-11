package com.umc.finly.domain.analysis.emotion.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EmotionAnalysisErrorCode implements BaseCode {

    // 4xx
    ANALYSIS_EMOTION_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS_EMOTION404_1", "종목을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}