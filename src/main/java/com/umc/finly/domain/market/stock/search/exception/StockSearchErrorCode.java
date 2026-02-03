package com.umc.finly.domain.market.stock.search.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StockSearchErrorCode implements BaseCode {
    INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "STOCK_SEARCH400", "검색어는 최소 2글자 이상이어야 합니다."),
    EMPTY_KEYWORD(HttpStatus.BAD_REQUEST, "STOCK_SEARCH400", "검색어가 비어 있습니다."),
    TOO_MANY_RESULTS(HttpStatus.BAD_REQUEST, "STOCK_SEARCH400", "검색 결과가 너무 많습니다. 검색어를 더 구체적으로 입력해주세요."),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_SEARCH404", "검색 결과를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
