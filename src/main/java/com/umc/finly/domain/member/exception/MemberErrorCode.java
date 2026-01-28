package com.umc.finly.domain.member.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "해당 이메일의 회원이 존재하지 않습니다."),

    PERSONA_ANSWERS_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400", "Q1~Q3 답안은 모두 제출해야 합니다."),
    INVALID_PERSONA_ANSWER(HttpStatus.BAD_REQUEST, "MEMBER401", "질문/선택지 매핑이 올바르지 않습니다."),
    INVALID_PERSONA_MODE(HttpStatus.BAD_REQUEST, "MEMBER403", "persona mode 값이 올바르지 않습니다."),
    PERSONA_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_2", "페르소나 결과를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}