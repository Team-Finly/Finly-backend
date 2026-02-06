package com.umc.finly.domain.analysis.association.exception;

import com.umc.finly.global.apiPayload.exception.CustomException;

public class AnalysisEntryException extends CustomException {

    public AnalysisEntryException(AnalysisEntryErrorCode errorCode) {super (errorCode);}

    public AnalysisEntryException(AnalysisEntryErrorCode errorCode, String message) {super (errorCode, message);}

    public AnalysisEntryException(AnalysisEntryErrorCode errorCode, String message, Throwable cause) {super (errorCode, message, cause);}
}
