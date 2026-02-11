package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.domain.analysis.association.exception.code.KoreaInvestErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;

public class KoreaInvestException extends CustomException {

    public KoreaInvestException(KoreaInvestErrorCode errorCode) {super (errorCode);}

    public KoreaInvestException(KoreaInvestErrorCode errorCode, String message) {super (errorCode, message);}

    public KoreaInvestException(KoreaInvestErrorCode errorCode, String message, Throwable cause) {super (errorCode, message, cause);}
}
