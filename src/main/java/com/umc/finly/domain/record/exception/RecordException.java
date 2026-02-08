package com.umc.finly.domain.record.exception;

import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.BaseCode;

// Record 도메인 전용 예외 클래스
// RecordErrorCode와 함께 사용하여 기록 관련 에러 처리
public class RecordException extends CustomException {
    public RecordException(BaseCode code) {
        super(code);
    }
}

