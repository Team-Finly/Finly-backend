package com.umc.finly.domain.record.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseCode {

    RECORD_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RECORD400_1", "필수값 또는 형식 오류입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
