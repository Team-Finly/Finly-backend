package com.umc.finly.domain.home.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HomeErrorCode implements BaseCode {

    // 홈 - 최근 나의 기록
    // 4xx
    HOME_MIND_ACCESS_DENIED(HttpStatus.FORBIDDEN, "HOME_403", "금융 마음 지수 조회 권한이 없습니다."),

    // 5xx
    HOME_WEEKLY_MOOD_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "HOME_500", "위클리 무드 조회 중 서버 오류가 발생했습니다.");




    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
