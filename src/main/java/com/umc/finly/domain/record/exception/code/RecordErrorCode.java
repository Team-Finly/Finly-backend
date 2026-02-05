package com.umc.finly.domain.record.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseCode {

    // 4xx
    RECORD_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RECORD400_1", "필수값 또는 형식 오류입니다."),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "RECORD400_2", "cursor 형식이 올바르지 않습니다."),
    INVALID_SIZE(HttpStatus.BAD_REQUEST, "RECORD400_3", "size는 1~50 사이여야 합니다."),
    INVALID_BOX_TYPE(HttpStatus.BAD_REQUEST, "RECORD400_4", "지원하지 않는 조각 모음함 타입입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
