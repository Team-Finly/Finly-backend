package com.umc.finly.domain.record.exception;

import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.BaseCode;

public class RecordException extends CustomException {
    public RecordException(BaseCode code) {
        super(code);
    }
}

