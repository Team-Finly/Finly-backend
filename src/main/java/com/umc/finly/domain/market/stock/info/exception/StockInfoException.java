package com.umc.finly.domain.market.stock.info.exception;

import com.umc.finly.domain.market.stock.info.exception.code.StockInfoErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;

public class StockInfoException extends CustomException {

    public StockInfoException(StockInfoErrorCode errorCode) {
        super(errorCode);
    }

    public StockInfoException(StockInfoErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public StockInfoException(StockInfoErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
