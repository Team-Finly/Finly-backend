package com.umc.finly.domain.analysis.association.exception.code;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AssociationErrorCode implements BaseCode {

    INVALID_ASSOCIATION_REQUEST(
            HttpStatus.BAD_REQUEST,
            "ASSOCIATION400_1",
            "잘못된 요청으로 기록한 종목 목록을 조회할 수 없습니다."
    ),//요청 오류

    INVALID_HEADER_FORMAT(
            HttpStatus.BAD_REQUEST,
            "ASSOCIATION400_2",
            "요청 헤더 형식이 올바르지 않습니다."
    ),//헤더 오류

    INVALID_QUERY_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "ASSOCIATION400_3",
            "요청 파라미터가 올바르지 않습니다."
    );//파라미터 오류


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


}
