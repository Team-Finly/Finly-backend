package com.umc.finly.domain.market.stock.exception;

import com.umc.finly.global.apiPayload.exception.CustomException;

public class StockInfoException extends CustomException {

    public StockInfoException(StockInfoErrorCode errorCode) {
        super(errorCode);
    }

    public StockInfoException(StockInfoErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
