package com.umc.finly.domain.market.stock.info.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StockInfoErrorCode implements BaseCode {
    KIS_FILE_DOWNLOAD_FAILED(HttpStatus.BAD_GATEWAY, "STOCK502", "KIS 종목 정보 파일 다운로드에 실패했습니다." ),
    KIS_FILE_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STOCK500", "KIS 종목 정보 파일 파싱에 실패했습니다."),
    TRADINGVIEW_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "STOCK502", "TradingView 심볼 페이지 요청에 실패했습니다." ),
    TRADINGVIEW_SYMBOL_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK404", "TradingView 심볼 페이지가 존재하지 않습니다."),
    TRADINGVIEW_LOGO_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK404", "TradingView 심볼 페이지에서 로고 이미지를 찾을 수 없습니다."),
    STOCK_INFO_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STOCK500", "종목 정보 저장 및 동기화 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
