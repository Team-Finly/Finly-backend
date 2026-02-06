package com.umc.finly.domain.analysis.emotion.exception;

import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.BaseCode;

public class EmotionAnalysisException extends CustomException {
    public EmotionAnalysisException(BaseCode code) {
        super(code);
    }
}
