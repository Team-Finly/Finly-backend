package com.umc.finly.domain.home.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HomeErrorCode implements BaseCode {

    // 홈 - 최근 나의 기록
    HOME_RECENT_RECORDS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "HOME_RECENT_RECORDS_NOT_FOUND",
            "최근 나의 기록이 존재하지 않습니다."
    ),

    HOME_RECENT_RECORDS_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "HOME_RECENT_RECORDS_ACCESS_DENIED",
            "최근 기록 조회 권한이 없습니다."
    ),

    HOME_RECENT_RECORDS_INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "HOME_RECENT_RECORDS_INTERNAL_ERROR",
            "최근 나의 기록 조회 중 서버 오류가 발생했습니다."
    ),

    HOME_WEEKLY_MOOD_INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "HOME_WEEKLY_MOOD_INTERNAL_ERROR",
            "위클리 무드 조회 중 서버 오류가 발생했습니다."
    ),

    HOME_MIND_MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "HOME_MIND_MEMBER_NOT_FOUND",
                    "사용자 정보를 찾을 수 없습니다."
    ),

    HOME_MIND_PERSONA_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "HOME_MIND_PERSONA_NOT_FOUND",
                    "사용자의 페르소나 정보를 찾을 수 없습니다."
    ),

    HOME_MIND_CALCULATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "HOME_MIND_CALCULATION_FAILED",
                    "금융 마음 지수 계산 중 오류가 발생했습니다."
    ),

    HOME_MIND_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "HOME_MIND_ACCESS_DENIED",
                    "금융 마음 지수 조회 권한이 없습니다."
    );



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
