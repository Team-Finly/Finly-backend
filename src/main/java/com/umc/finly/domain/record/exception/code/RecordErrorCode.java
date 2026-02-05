package com.umc.finly.domain.record.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseCode {

    // RECORD 4xx
    RECORD_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RECORD400_1", "필수값 또는 형식 오류입니다."),
    INVALID_BOX_TYPE(HttpStatus.BAD_REQUEST, "RECORD400_2", "지원하지 않는 조각 모음함 타입입니다."),
    INVALID_PERIOD_KEY(HttpStatus.BAD_REQUEST, "RECORD400_3", "지원하지 않는 기간 설정입니다."),
    RECORD_FORBIDDEN(HttpStatus.FORBIDDEN, "RECORD403_1", "해당 기록에 대한 권한이 없습니다."),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD404_2", "기록을 찾을 수 없습니다."),
    RECORD_DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "RECORD409_3", "중복 제출된 요청입니다."),

    // FEEDBACK 4xx
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK404_1", "피드백을 찾을 수 없습니다."),
    FEEDBACK_GENERATION_IN_PROGRESS(HttpStatus.CONFLICT, "FEEDBACK409_1", "피드백이 생성 중입니다."),

    // FEEDBACK 5xx
    FEEDBACK_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FEEDBACK500_1", "피드백 생성에 실패했습니다."),
    OPENAI_API_FAILED(HttpStatus.BAD_GATEWAY, "FEEDBACK502_1", "OpenAI API 호출에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
