package com.umc.finly.domain.member.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseCode {

    // 400
    PERSONA_ANSWERS_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_1", "Q1~Q3 답안은 모두 제출해야 합니다."),
    INVALID_PERSONA_ANSWER(HttpStatus.BAD_REQUEST, "MEMBER400_2", "질문/선택지 매핑이 올바르지 않습니다."),
    INVALID_PERSONA_MODE(HttpStatus.BAD_REQUEST, "MEMBER400_3", "persona mode 값이 올바르지 않습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "MEMBER400_4", "유효하지 않은 이미지 파일입니다"),
    PROFILE_IMAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER400_5", "이미 프로필 사진이 존재합니다."),

    // 404
    PERSONA_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "페르소나 결과를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_2", "멤버를 찾을 수 없습니다."),
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_3", "프로필 이미지를 찾을 수 없습니다."),

    // 413
    IMAGE_FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "MEMBER413", "이미지 파일 용량이 너무 큽니다."),

    // 500
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER500_1", "이미지 업로드에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}