package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.domain.analysis.association.exception.code.ConvictionScoreErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;

public class ConvictionScoreException extends CustomException {

    public ConvictionScoreException(ConvictionScoreErrorCode errorCode) {super (errorCode);}

    public ConvictionScoreException(ConvictionScoreErrorCode errorCode, String message) {super (errorCode, message);}

    public ConvictionScoreException(ConvictionScoreErrorCode errorCode, String message, Throwable cause) {super (errorCode, message, cause);}
}
