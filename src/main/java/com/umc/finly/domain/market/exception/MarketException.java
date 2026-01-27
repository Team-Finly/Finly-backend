package com.umc.finly.domain.market.exception;

import com.umc.finly.domain.market.exception.code.MarketErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;

public class MarketException extends CustomException {

    public MarketException(MarketErrorCode errorCode) {
        super(errorCode);
    }
}
