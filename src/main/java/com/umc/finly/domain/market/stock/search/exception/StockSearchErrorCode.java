package com.umc.finly.domain.market.stock.search.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StockSearchErrorCode implements BaseCode {

    EMPTY_KEYWORD(HttpStatus.BAD_REQUEST, "STOCK_SEARCH400", "검색어가 비어 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
