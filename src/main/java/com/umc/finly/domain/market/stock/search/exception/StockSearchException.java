package com.umc.finly.domain.market.stock.search.exception;

import com.umc.finly.domain.market.stock.info.exception.StockInfoErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;

public class StockSearchException extends CustomException {

    public StockSearchException(StockInfoErrorCode errorCode) {super (errorCode);}

    public StockSearchException(StockInfoErrorCode errorCode, String message) {super (errorCode, message);}

    public StockSearchException(StockInfoErrorCode errorCode, String message, Throwable cause) {super (errorCode, message, cause);}
}
