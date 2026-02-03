package com.umc.finly.global.apiPayload.exception;

import com.umc.finly.global.apiPayload.response.BaseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final BaseCode errorCode;

    public CustomException(BaseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(BaseCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomException(BaseCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
