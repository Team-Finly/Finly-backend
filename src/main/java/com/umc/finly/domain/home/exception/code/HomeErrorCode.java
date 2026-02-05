package com.umc.finly.domain.home.exception.code;

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
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
