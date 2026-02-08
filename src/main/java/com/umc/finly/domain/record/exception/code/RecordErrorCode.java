package com.umc.finly.domain.record.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// Record 도메인 에러 코드 enum
// 기록 및 피드백 관련 에러 정의
// 코드 형식: {도메인}{HTTP상태}_{순번} (예: RECORD400_1)
@Getter
@AllArgsConstructor
public enum RecordErrorCode implements BaseCode {

    // ===== RECORD 에러 (4xx) =====
    RECORD_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RECORD400_1", "필수값 또는 형식 오류입니다."),       // 유효성 검증 실패
    INVALID_BOX_TYPE(HttpStatus.BAD_REQUEST, "RECORD400_2", "지원하지 않는 조각 모음함 타입입니다."),     // 잘못된 EmotionCode
    INVALID_PERIOD_KEY(HttpStatus.BAD_REQUEST, "RECORD400_3", "지원하지 않는 기간 설정입니다."),         // 잘못된 FragmentPeriodKey
    RECORD_FORBIDDEN(HttpStatus.FORBIDDEN, "RECORD403_1", "해당 기록에 대한 권한이 없습니다."),         // 다른 사용자 기록 접근 시도
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD404_2", "기록을 찾을 수 없습니다."),                // 존재하지 않는 recordId
    RECORD_DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "RECORD409_3", "중복 제출된 요청입니다."),        // clientRequestId 중복 (멱등성)

    // ===== FEEDBACK 에러 (4xx) =====
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK404_1", "피드백을 찾을 수 없습니다."),           // 존재하지 않는 feedbackId
    FEEDBACK_GENERATION_IN_PROGRESS(HttpStatus.CONFLICT, "FEEDBACK409_1", "피드백이 생성 중입니다."),  // 이미 GENERATING 상태

    // ===== FEEDBACK 에러 (5xx) =====
    FEEDBACK_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FEEDBACK500_1", "피드백 생성에 실패했습니다."), // 내부 처리 실패
    OPENAI_API_FAILED(HttpStatus.BAD_GATEWAY, "FEEDBACK502_1", "OpenAI API 호출에 실패했습니다.");               // OpenAI 연동 실패

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String code;           // 에러 코드 (클라이언트 식별용)
    private final String message;        // 에러 메시지 (사용자 표시용)
}
