package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.domain.analysis.association.exception.code.FearIndexErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;

public class FearIndexException extends CustomException {

    public FearIndexException(FearIndexErrorCode errorCode) {super (errorCode);}

    public FearIndexException(FearIndexErrorCode errorCode, String message) {super (errorCode, message);}

    public FearIndexException(FearIndexErrorCode errorCode, String message, Throwable cause) {super (errorCode, message, cause);}
}
